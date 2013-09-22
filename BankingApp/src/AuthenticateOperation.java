	//
//  AuthenticateOperation.m
//  Mobile Banking
//
//  Created by Nathan Jones on 2/17/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

//#import "AuthenticateOperation.h"

//#define kEndpoint @"/user/authenticate"
//#define kHTTPMethod @"POST"
//#define kOperationTimeout 30.0
import java.net.HttpURLConnection;

public class AuthenticateOperation
{
	HttpURLConnection connection;
	
String username; //= _username;
String password; //= _password;
String passphrase;// = _passphrase;
String registerDevice;// = _registerDevice;

static boolean _operationInProcess = false;

boolean isExecuting = false;
boolean isFinished = false;


// used to allow for asynchronous request
 boolean isConcurrent() {
    return true;
}

void start() {
    
    if (![NSThread isMainThread]) {
        [self performSelectorOnMainThread:@selector(start) 
                               withObject:nil 
                            waitUntilDone:NO];
        return;
    }
    
    [self willChangeValueForKey:@"isExecuting"];
    isExecuting = YES;
    [self didChangeValueForKey:@"isExecuting"];
    
    // alert observers
    dispatch_async(dispatch_get_main_queue(), ^{
        [[NSNotificationCenter defaultCenter] postNotificationName:kNormalLoginStartNotification object:nil];
    });
    
    // build and issue our authentication request
    NSString *url;
    url = [kResourceBaseURL stringByAppendingString:kEndpoint];
    if (_registerDevice == YES) {
        url = [url stringByAppendingFormat:@"?register=true&pin=%@", _passphrase];
    }
    
    NSMutableURLRequest *request = [self buildRequestWithURL:url
                                                  httpMethod:kHTTPMethod
                                                     payload:nil
                                                     timeout:kOperationTimeout
                                               signingOption:NJRequestSigningOptionNone];
    
    connection = [[NSURLConnection alloc] initWithRequest:request
                                                 delegate:self];
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    
}

void finish()  {
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    
    connection = nil;
    
    [self willChangeValueForKey:@"isExecuting"];
    [self willChangeValueForKey:@"isFinished"];
    
    isExecuting = NO;
    isFinished = YES;
    
    [self didChangeValueForKey:@"isExecuting"];
    [self didChangeValueForKey:@"isFinished"];
}

boolean operationInProcess() {
    return _operationInProcess;
}

#pragma mark - Copy
- (id)copyWithZone:(NSZone *)zone {
    AuthenticateOperation *copy = [super copyWithZone:zone];
    copy.username = _username;
    copy.password = _password;
    return copy;
}

#pragma mark - NSURLConnection Delegates
- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    self.responseData = [[NSMutableData alloc] init];
    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
    self.statusCode = httpResponse.statusCode;
}

void connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    [self.responseData appendData:data];
}

void connectionDidFinishLoading:(NSURLConnection *)connection {
    
    // successful authentication
    if (self.statusCode == 200) {
        
        // unpack service response
        NSError *error = nil;
        NSDictionary *response = [NSJSONSerialization JSONObjectWithData:self.responseData 
                                                                 options:0 
                                                                   error:&error];
        
        NSString *token = [response objectForKey:@"token"];
        // normally we would write the token to the keychain, Chapter XX covers keychain
        // in more detail, so for now we'll store in our model object
        [Model sharedModel].token = token;
        
        // create our client certificate if necessary, and store in the credential store
        if (_registerDevice == YES) {
            
            NSString *certString = [response objectForKey:@"certificate"];
            NSData *certData = [NSData dataWithBase64EncodedString:certString];
        
            SecIdentityRef identity = NULL;
            SecCertificateRef certificate = NULL;
            [Utils identity:&identity andCertificate:&certificate fromPKCS12Data:certData withPassphrase:_passphrase];
            
            // store the certificate for future authentication challenges
            if (identity != NULL) {
                
                // store the certificate and identity in the keychain
                NSArray *certArray = [NSArray arrayWithObject:(__bridge id)certificate];
                NSURLCredential *credential = [NSURLCredential credentialWithIdentity:identity
                                                                         certificates:certArray
                                                                          persistence:NSURLCredentialPersistencePermanent];
                
                [[NSURLCredentialStorage sharedCredentialStorage] setDefaultCredential:credential
                                                             forProtectionSpace:[[Model sharedModel] clientCertificateProtectionSpace]];

                // we got what we needed, save it for later
                [[Model sharedModel] registerDevice];
            }
        }

        
        // issue command to retrieve users accounts
        [[Model sharedModel] fetchAccounts];
        
        // alert observers
        dispatch_async(dispatch_get_main_queue(), ^{
            [[NSNotificationCenter defaultCenter] postNotificationName:kNormalLoginSuccessNotification object:nil];
        });
        
    // authentication failed
    } else {
        
        // alert observers
        dispatch_async(dispatch_get_main_queue(), ^{
            [[NSNotificationCenter defaultCenter] postNotificationName:kNormalLoginFailedNotification object:nil];
        });
    }

    [self finish];
}

void connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    // post notification
    dispatch_async(dispatch_get_main_queue(), ^{
        [[NSNotificationCenter defaultCenter] postNotificationName:kNormalLoginFailedNotification object:nil];
    });
    
    [self finish];
}


void connection:(NSURLConnection *)connection willSendRequestForAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge {
    
    // validate that the authentication challenge came from a whitelisted protection space
    if (![[[Model sharedModel] validProtectionSpaces] containsObject:challenge.protectionSpace]) {
        // dispatch alert view message to the main thread
        dispatch_async(dispatch_get_main_queue(), ^{
            [[[UIAlertView alloc] initWithTitle:@"Unsecure Connection"
                                        message:@"We're unable to establish a secure connection. Please check your network connection and try again."
                                       delegate:nil
                              cancelButtonTitle:@"OK"
                              otherButtonTitles:nil] show];
        });
        
        // cancel authentication
        [challenge.sender cancelAuthenticationChallenge:challenge];
    }
    
    // respond to basic authentication requests
    // others that follow this pattern include DIGEST and NTLM authentication
    if (challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodHTTPBasic) {

        // proceed with authentication
        if (challenge.previousFailureCount == 0) {
            NSURLCredential *creds = [[NSURLCredential alloc] initWithUser:_username
                                                                  password:_password 
                                                               persistence:NSURLCredentialPersistenceForSession];
            
            [challenge.sender useCredential:creds
                 forAuthenticationChallenge:challenge];
            
        // authentication has previously failed. depending on authentication configuration, too
        // many attempts here could lead to a poor user experience via locked accounts
        } else {
            
            // cancel the authentication attempt
            [[challenge sender] cancelAuthenticationChallenge:challenge];
            
            // alert observers of the failed attempt
            dispatch_async(dispatch_get_main_queue(), ^{
                [[NSNotificationCenter defaultCenter] postNotificationName:kNormalLoginFailedNotification object:nil];
            });
            
        }
        
    }
}

@end
}
