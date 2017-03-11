/**
  * APICloud Modules
  * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
  * Licensed under the terms of the The MIT License (MIT).
  * Please see the license.html included with this distribution for details.
  */


#import "UIMultiSelector.h"
#import "NSDictionaryUtils.h"
#import "UZAppUtils.h"

@interface UIMultiSelector ()<UIGestureRecognizerDelegate>
{
    NSInteger openCbId;
    float coordy;
    BOOL animation;
    UIView *_maskView;    //遮罩层
}
@property(assign,nonatomic)BOOL *maskClose;
@end

@implementation UIMultiSelector

@synthesize fontColor,selectedColor;
@synthesize dataSource = _dataSource;
@synthesize multiPickerView=_multiPickerView;

- (id)initWithUZWebView:(UZWebView *)webView {
    self = [super initWithUZWebView:webView];
    if (self) {
        openCbId = -1;
    }
    return self;
}

-(void)dispose {
    if (_dataSource) {
        self.dataSource = nil;
    }
    if (_multiPickerView) {
        [_multiPickerView removeFromSuperview];
        self.multiPickerView = nil;
    }
    self.fontColor = nil;
    self.selectedColor = nil;
    [self close:nil];
    if ( openCbId!=-1) {
        [self deleteCallback: openCbId];
    }
}

- (void)open:(NSDictionary*)paramsDict_ {
    if (_multiPickerView) {
        [self show:nil];
        return;
    }
    if ([paramsDict_ objectForKey:@"cbId"]) {
         openCbId = [[paramsDict_ objectForKey:@"cbId"]intValue];
    }
    float screenHeight = [UIScreen mainScreen].bounds.size.height;
    float screenWidth = [UIScreen mainScreen].bounds.size.width;
    NSDictionary *rectInfo = [paramsDict_ dictValueForKey:@"rect" defaultValue:@{}];
    CGFloat frameH = [rectInfo floatValueForKey:@"h" defaultValue:244.0];
    coordy = screenHeight - frameH;
    NSDictionary *styleInfo = [paramsDict_ dictValueForKey:@"styles" defaultValue:@{}];
    //maskColor、maskView
    self.maskClose = [paramsDict_ boolValueForKey:@"maskClose" defaultValue:YES];
    NSString *maskColor = [styleInfo stringValueForKey:@"mask" defaultValue:@"rgba(0,0,0,0)"];
    if ([maskColor isKindOfClass:[NSString class]] && maskColor.length <= 0) {
        maskColor = @"rgba(0,0,0,0)";
    }
    _maskView = [[UIView alloc]initWithFrame:[UIScreen mainScreen].bounds] ;
    if ([UZAppUtils isValidColor:maskColor]) {
        _maskView.backgroundColor = [UZAppUtils colorFromNSString:maskColor];
    } else {
        UIImageView *bgImgView = [[UIImageView alloc]initWithFrame:_maskView.bounds];
        _maskView.backgroundColor = [UIColor clearColor];
        NSString *maskPath = [self getPathWithUZSchemeURL:maskColor];
        bgImgView.image = [UIImage imageWithContentsOfFile:maskPath];
        [_maskView addSubview:bgImgView];
    }
    if ([paramsDict_ objectForKey:@"animation"]) {
        animation = [paramsDict_ boolValueForKey:@"animation" defaultValue:YES];
    }
    self.dataSource = [paramsDict_ arrayValueForKey:@"items" defaultValue:nil];
    if (_dataSource ==nil ||[_dataSource count]<=0) {
        return;
    }
    //点击后删除之前的PickerView
    for (UIView *view in self.viewController.view.subviews) {
        if ([view isKindOfClass:[UIMultiSelectorPickerView class]]) {
            [view removeFromSuperview];
        }
    }
    _multiPickerView = [[UIMultiSelectorPickerView alloc] initWithFrame:CGRectMake(0,screenHeight, screenWidth,frameH) withInterfaceInfo:paramsDict_];       //先隐藏在视图下面，便于添加动画
    _multiPickerView.entriesArray = _dataSource;
    _multiPickerView.multiPickerDelegate = self;
    if (_dataSource != nil && _dataSource.count>0) {
        _multiPickerView.selectedItems = _dataSource;
    }
    [self.viewController.view addSubview:_maskView];
    [_maskView addSubview:_multiPickerView];
    [[_maskView superview] bringSubviewToFront:_maskView];

    [_multiPickerView pickerShow];
    [self show:nil];
    [self sendResultEventWithCallbackId:openCbId dataDict:@{@"eventType":@"show"} errDict:nil doDelete:NO];
    
    UITapGestureRecognizer *maskTap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(clickMaskViewClose:)];
    maskTap.delegate = self;
    [_maskView addGestureRecognizer:maskTap];
}

-(void)show:(NSDictionary*)parmasDict_ {
    if (!_multiPickerView) {
        return;
    }
    if (animation) {
        _maskView.hidden = NO;
        [UIView animateWithDuration:0.3 animations:^{
            CGRect tempRect = _multiPickerView.frame;
            tempRect.origin.y = coordy;
            _multiPickerView.frame = tempRect;
        } completion:^(BOOL finished) {
        }];
    } else {
        CGRect tempRect = _multiPickerView.frame;
        tempRect.origin.y = coordy;
        _multiPickerView.frame = tempRect;
        _maskView.hidden = NO;
    }
}

-(void)hide:(NSDictionary*)parmasDict_ {
    if (!_multiPickerView) {
        return;
    }
    if (animation) {
        [UIView animateWithDuration:0.3 animations:^{
            CGRect tempRect = _multiPickerView.frame;
            tempRect.origin.y = [UIScreen mainScreen].bounds.size.height;
            _multiPickerView.frame = tempRect;
        } completion:^(BOOL finished){
            _maskView.hidden = YES;
        }];
    } else {
        CGRect tempRect = _multiPickerView.frame;
        tempRect.origin.y = [UIScreen mainScreen].bounds.size.height;
        _multiPickerView.frame = tempRect;
        _maskView.hidden = YES;
    }
}

- (void)close:(NSDictionary*)parmasDict_ {
    if (!_multiPickerView) {
        return;
    }
    if (animation) {
        [UIView animateWithDuration:0.3 animations:^{
            CGRect tempRect = _multiPickerView.frame;
            tempRect.origin.y = [UIScreen mainScreen].bounds.size.height;
            _multiPickerView.frame = tempRect;
        } completion:^(BOOL finished){
            if (_maskView) {
                [_maskView removeFromSuperview];
                _maskView = nil;
            }
            if (_multiPickerView) {
                _multiPickerView.multiPickerDelegate = nil;
                [_multiPickerView removeFromSuperview];
                self.multiPickerView = nil;
            }
        }];
    } else {
        if (_maskView) {
            [_maskView removeFromSuperview];
            _maskView = nil;
        }
        if (_multiPickerView) {
            _multiPickerView.multiPickerDelegate = nil;
            [_multiPickerView removeFromSuperview];
            self.multiPickerView = nil;
        }
    }
}

#pragma mark - Delegate
//点击左上角按钮
-(void)returnLeftBtnClickPickerString:(NSMutableArray *)selectedEntriesArr {
    NSMutableDictionary * sendDict = [NSMutableDictionary dictionaryWithCapacity:2];
    [sendDict setObject:@"clickLeft" forKey:@"eventType"];
    [sendDict setObject:selectedEntriesArr forKey:@"items"];
    [self sendResultEventWithCallbackId: openCbId dataDict:sendDict errDict:nil doDelete:NO];
}

//点击右上角按钮
-(void)returnRightBtnClickPickerString:(NSMutableArray *)selectedEntriesArr {
    NSMutableDictionary * sendDict = [NSMutableDictionary dictionaryWithCapacity:2];
    [sendDict setObject:@"clickRight" forKey:@"eventType"];
    [sendDict setObject:selectedEntriesArr forKey:@"items"];
    [self sendResultEventWithCallbackId: openCbId dataDict:sendDict errDict:nil doDelete:NO];
    //    NSString *dataStr = [selectedEntriesArr componentsJoinedByString:@"\n"];
}

- (void)clickMaskViewClose:(UIGestureRecognizer *)tapGR {
    
    if (self.maskClose) {
        
        [self close:nil];
        
    }
        NSMutableDictionary * sendDict = [NSMutableDictionary dictionaryWithCapacity:2];
        [sendDict setObject:@"clickMask" forKey:@"eventType"];
        [self sendResultEventWithCallbackId: openCbId dataDict:sendDict errDict:nil doDelete:NO];
    
   
}

- (void)returnMaxSelectedItem:(NSDictionary *)overflowTip {
    [self sendResultEventWithCallbackId:openCbId dataDict:overflowTip errDict:nil doDelete:NO];
}

-(void)cancelClick {
    [self hide:nil];
}

- (NSString *)getRealPath:(NSString *)paramPath {
    return [self getPathWithUZSchemeURL:paramPath];
}

//屏蔽点击 _maskView 的子view，子view 也响应_maskView的点击手势事件
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch {
    if (touch.view != _maskView) {
        return NO;
    }
    return YES;
}

@end

