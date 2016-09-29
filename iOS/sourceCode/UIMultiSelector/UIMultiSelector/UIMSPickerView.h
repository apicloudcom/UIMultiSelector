/**
  * APICloud Modules
  * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
  * Licensed under the terms of the The MIT License (MIT).
  * Please see the license.html included with this distribution for details.
  */


#import <UIKit/UIKit.h>

@protocol UIMSPickerViewDelegate;

@interface UIMSPickerView : UIView <UITableViewDataSource, UITableViewDelegate>
{
  @private
  UITableView *internalTableView_;
}

// Set a delegate conforming to UIMSPickerViewDelegate protocol
@property (nonatomic, weak) id<UIMSPickerViewDelegate> delegate;
// If set to nil the all option row is hidden at all, default is 'All'
@property (nonatomic, assign) BOOL singleSelector;
@property (nonatomic, copy) NSString *allOptionTitle;
@property (nonatomic, copy) NSString *bgImg;
@property (nonatomic, strong) UIColor *selectColor;
@property (nonatomic, strong) UIColor *normalColor;
@property (nonatomic, strong) NSArray *selectedItems;
@property (nonatomic, assign) NSInteger maxOfSelected;
@property (nonatomic, strong) NSDictionary *itemStyle;
@property (nonatomic, strong) NSDictionary *iconStyle;
@property (nonatomic, strong) UITableView *internalTableView_;

-(void)addAllMSPickerViewCells;

- (instancetype)initWithFrame:(CGRect)frame withAllTitle:(NSString *)allTitle;

@end

@protocol UIMSPickerViewDelegate <NSObject>

// Return the number of elements of your pickerview
- (NSInteger)numberOfRowsForPickerView:(UIMSPickerView *)pickerView;
// Return a plain UIString to display on the given row
- (NSString *)pickerView:(UIMSPickerView *)pickerView textForRow:(NSInteger)row;
// Return a boolean selection state on the given row
- (BOOL)pickerView:(UIMSPickerView *)pickerView selectionStateForRow:(NSInteger)row;

@optional
// Inform the delegate that a row got selected, if row = -1 all rows are selected
- (void)pickerView:(UIMSPickerView *)pickerView didCheckRow:(NSInteger)row;
// Inform the delegate that a row got deselected, if row = -1 all rows are deselected
- (void)pickerView:(UIMSPickerView *)pickerView didUncheckRow:(NSInteger)row;
- (void)pickerView:(UIMSPickerView *)pickerView didUncheckRowZero:(NSInteger)row;
- (NSString *)getRealPath:(NSString *)paramPath;

- (void)returnMaxTip:(NSDictionary *)maxDict;
- (void)returnSelectedItems;

@end
