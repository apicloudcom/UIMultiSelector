/**
  * APICloud Modules
  * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
  * Licensed under the terms of the The MIT License (MIT).
  * Please see the license.html included with this distribution for details.
  */


#import <Foundation/Foundation.h>
#import "UZModule.h"
#import "UIMSPickerView.h"
#import "UIMultiSelectorPickerView.h"

@interface UIMultiSelector : UZModule <UZUIMultiSelectPickerViewDelegate> 

@property (nonatomic,strong) UIColor * fontColor;
@property (nonatomic,strong) UIColor * selectedColor;
@property (nonatomic,strong) NSArray * dataSource;
@property (nonatomic,strong) UIMultiSelectorPickerView *multiPickerView;

@end
