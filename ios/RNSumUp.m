//
//  RNSumUp.m
//  RNSumUp.m
//
//  Created by Ítalo Menezes on 26/02/2018.
//  Copyright © 2018 NEXTAR. All rights reserved.
//

#import "RNSumUp.h"
#import "AppDelegate.h"

@implementation RCTConvert (SMPPaymentOptions)

RCT_ENUM_CONVERTER(SMPPaymentOptions, (
                                       @{@"SMPPaymentOptionAny"           : @(SMPPaymentOptionAny),
                                         @"SMPPaymentOptionCardReader"    : @(SMPPaymentOptionCardReader),
                                         @"SMPPaymentOptionMobilePayment" : @(SMPPaymentOptionMobilePayment)
                                         }), SMPPaymentOptionAny, integerValue);

@end

@implementation RNSumUp

- (NSDictionary *)constantsToExport
{
    return @{ @"SMPPaymentOptionAny": @(SMPPaymentOptionAny),
              @"SMPPaymentOptionCardReader": @(SMPPaymentOptionCardReader),
              @"SMPPaymentOptionMobilePayment": @(SMPPaymentOptionMobilePayment)};
}

RCT_EXPORT_METHOD(setup:(NSString *)key resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    BOOL setupResponse = [SMPSumUpSDK setupWithAPIKey:key];
    if (setupResponse) {
        resolve(nil);
    } else {
        reject(@"000", @"It was not possible to complete setup with SumUp SDK. Please, check your implementation.", nil);
    }
}

RCT_EXPORT_METHOD(authenticate:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_sync(dispatch_get_main_queue(), ^{
        UIViewController *rootViewController = UIApplication.sharedApplication.delegate.window.rootViewController;
        [SMPSumUpSDK presentLoginFromViewController:rootViewController animated:YES
                                    completionBlock:^(BOOL success, NSError *error) {
                                        if (error) {
                                            [rootViewController dismissViewControllerAnimated:YES completion:nil];
                                            reject(@"000", @"It was not possible to auth with SumUp. Please, check the username and password provided.", error);
                                        } else {
                                            resolve(@{@"success": @(success)});
                                        }
                                    }];
    });
}

RCT_EXPORT_METHOD(logout:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [SMPSumUpSDK logoutWithCompletionBlock:^(BOOL success, NSError * _Nullable error) {
        if (!success) {
            reject(@"004", @"It was not possible to log out with SumUp. Please, try again.", nil);
        } else {
            resolve(nil);
        }
    }];
}

RCT_EXPORT_METHOD(prepareForCheckout:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    BOOL isLoggedIn = [SMPSumUpSDK isLoggedIn];
    if (isLoggedIn) {
        [SMPSumUpSDK prepareForCheckout];
        resolve(nil);
    } else {
        reject(@"003", @"It was not possible to prepare for checkout. Please, log in first.", nil);
    }
}

RCT_EXPORT_METHOD(checkout:(NSDictionary *)request resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    NSDecimalNumber *total = [NSDecimalNumber decimalNumberWithString:[RCTConvert NSString:request[@"totalAmount"]]];
    NSString *title = [RCTConvert NSString:request[@"title"]];
    NSString *currencyCode = [RCTConvert NSString:request[@"currencyCode"]];
    NSUInteger paymentOption = [RCTConvert SMPPaymentOptions:request[@"paymentOption"]];
    SMPCheckoutRequest *checkoutRequest = [SMPCheckoutRequest requestWithTotal:total
                                                                         title:title
                                                                  currencyCode:currencyCode
                                                                paymentOptions:paymentOption];
    dispatch_sync(dispatch_get_main_queue(), ^{
        UIViewController *rootViewController = UIApplication.sharedApplication.delegate.window.rootViewController;
        [SMPSumUpSDK checkoutWithRequest:checkoutRequest
                      fromViewController:rootViewController
                              completion:^(SMPCheckoutResult *result, NSError *error) {
                                  if (error) {
                                      reject(@"001", @"It was not possible to perform checkout with SumUp. Please, try again.", error);
                                  } else {
                                      resolve(@{@"success": @([result success]), @"transactionCode": [result transactionCode]});
                                  }
                              }];
    });
}

RCT_EXPORT_METHOD(preferences:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_sync(dispatch_get_main_queue(), ^{
        UIViewController *rootViewController = UIApplication.sharedApplication.delegate.window.rootViewController;
        [SMPSumUpSDK presentCheckoutPreferencesFromViewController:rootViewController
                                                         animated:YES
                                                       completion:^(BOOL success, NSError * _Nullable error) {
                                                           if (!success) {
                                                               resolve(nil);
                                                           } else {
                                                               reject(@"002", @"It was not possible to open Preferences window. Please, try again.", nil);
                                                           }
                                                       }];
    });
}

RCT_EXPORT_METHOD(isLoggedIn:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    BOOL isLoggedIn = [SMPSumUpSDK isLoggedIn];
    resolve(@{@"isLoggedIn": @(isLoggedIn)});
}


RCT_EXPORT_MODULE();

@end


