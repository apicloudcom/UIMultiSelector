/**
  * APICloud Modules
  * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
  * Licensed under the terms of the The MIT License (MIT).
  * Please see the license.html included with this distribution for details.
  */


#import <UIKit/UIKit.h>
#import "UIMSPickerView.h"

@protocol UZUIMultiSelectPickerViewDelegate <NSObject>

- (void)cancelClick;
- (NSString *)getRealPath:(NSString *)paramPath;

@required
- (void)returnLeftBtnClickPickerString:(NSMutableArray *)selectedEntriesArr;
- (void)returnRightBtnClickPickerString:(NSMutableArray *)selectedEntriesArr;
- (void)returnMaxSelectedItem:(NSDictionary *)overflowTip;

@end

@interface UIMultiSelectorPickerView : UIView

@property (nonatomic, strong) NSArray *entriesArray;
@property (nonatomic, strong) NSArray *entriesSelectedArray;
@property (nonatomic, weak) id<UZUIMultiSelectPickerViewDelegate> multiPickerDelegate;
@property (nonatomic,strong) NSArray *selectedItems;
@property (nonatomic, strong) UIMSPickerView *pickerView;

- (void)pickerShow;
- (id)initWithFrame:(CGRect)frame withInterfaceInfo:(NSDictionary *)paramsDic_ ;

@end
