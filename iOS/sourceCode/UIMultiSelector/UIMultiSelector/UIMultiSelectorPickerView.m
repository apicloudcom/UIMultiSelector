/**
  * APICloud Modules
  * Copyright (c) 2014-2017 by APICloud, Inc. All Rights Reserved.
  * Licensed under the terms of the The MIT License (MIT).
  * Please see the license.html included with this distribution for details.
  */

#import "UIMultiSelectorPickerView.h"
#import "NSDictionaryUtils.h"
#import "UZAppUtils.h"

@interface UIMultiSelectorPickerView()<UIMSPickerViewDelegate>
{
    NSDictionary *_allParams;
    NSInteger selectMax;
    BOOL singleSelection;
}

@property (nonatomic, strong) NSMutableDictionary *selectionStatesDic;

@end

@implementation UIMultiSelectorPickerView

- (id)initWithFrame:(CGRect)frame withInterfaceInfo:(NSDictionary *)paramsDic_ {
    self = [super initWithFrame:frame];
    if (self) {
        _allParams = [[NSDictionary alloc]initWithDictionary:paramsDic_];

        self.selectionStatesDic = [[NSMutableDictionary alloc] initWithCapacity:16];
        
        self.entriesArray = [[NSMutableArray alloc] initWithCapacity:16];
        self.entriesSelectedArray = [[NSMutableArray alloc] initWithCapacity:16];
    }
    return self;
}

-(void)dealloc {
    self.entriesArray = nil;
    self.selectionStatesDic = nil;
    self.pickerView = nil;
}

- (void)pickerShow {
    //styles
    NSDictionary *styleInfo = [_allParams dictValueForKey:@"styles" defaultValue:@{}];
    //item
    NSDictionary *itemStyle = [styleInfo dictValueForKey:@"item" defaultValue:@{}];
    //icon
    NSDictionary *iconStyle = [styleInfo dictValueForKey:@"icon" defaultValue:@{}];
    NSDictionary *textInfo = [_allParams dictValueForKey:@"text" defaultValue:@{}];
    NSString *centerTitle = [textInfo stringValueForKey:@"title" defaultValue:@""];
    NSString *lBtnTitle = [textInfo stringValueForKey:@"leftBtn" defaultValue:@"取消"];
    NSString *rBtnTitle = [textInfo stringValueForKey:@"rightBtn" defaultValue:@"完成"];
    NSString *selectAll = [textInfo stringValueForKey:@"selectAll" defaultValue:@"全选"];
    if([selectAll isKindOfClass:[NSString class]] && selectAll.length <= 0) {
        selectAll = @"全选";
    }
    //max
    selectMax = [_allParams integerValueForKey:@"max" defaultValue:0];
    singleSelection = [_allParams boolValueForKey:@"singleSelection" defaultValue:false];
    if (singleSelection) {
        selectMax = 1;
    }
    [self initSelectionStatesDic];

    //styles.title
    NSDictionary *titleStyle = [styleInfo dictValueForKey:@"title" defaultValue:@{}];
    NSString *titleBg = [titleStyle stringValueForKey:@"bg" defaultValue:@"#ddd"];
    NSString *titleColor = [titleStyle stringValueForKey:@"color" defaultValue:@"#444"];
    NSInteger titleFont = [titleStyle integerValueForKey:@"size" defaultValue:16];
    CGFloat titleHigh = [titleStyle floatValueForKey:@"h" defaultValue:44];
    if ([titleBg isKindOfClass:[NSString class]] && titleBg.length <= 0) {
        titleBg = @"ddd";
    }
    if ([titleColor isKindOfClass:[NSString class]] && titleColor.length <= 0) {
        titleColor = @"444";
    }
    //styles.leftButton
    NSDictionary *lBtnStyle = [styleInfo dictValueForKey:@"leftButton" defaultValue:@{}];
    NSString *lBtnBg = [lBtnStyle stringValueForKey:@"bg" defaultValue:@"#f00"];
    CGFloat lBtnW = [lBtnStyle floatValueForKey:@"w" defaultValue:80];
    CGFloat lBtnH = [lBtnStyle floatValueForKey:@"h" defaultValue:35];
    CGFloat marginTL = [lBtnStyle floatValueForKey:@"marginT" defaultValue:5];
    CGFloat marginLL = [lBtnStyle floatValueForKey:@"marginL" defaultValue:8];
    NSString *titleColorL = [lBtnStyle stringValueForKey:@"color" defaultValue:@"#fff"];
    NSInteger titleFontL = [lBtnStyle integerValueForKey:@"size" defaultValue:14];
    if ([lBtnBg isKindOfClass:[NSString class]] && lBtnBg.length <= 0) {
        lBtnBg = @"f00";
    }
    if ([titleColorL isKindOfClass:[NSString class]] && titleColorL.length <= 0) {
        titleColorL = @"fff";
    }
    //styles.rightButton
    NSDictionary *rBtnStyle = [styleInfo dictValueForKey:@"rightButton" defaultValue:@{}];
    NSString *rBtnBg = [rBtnStyle stringValueForKey:@"bg" defaultValue:@"#0f0"];
    CGFloat rBtnW = [rBtnStyle floatValueForKey:@"w" defaultValue:80];
    CGFloat rBtnH = [rBtnStyle floatValueForKey:@"h" defaultValue:35];
    CGFloat marginTR = [rBtnStyle floatValueForKey:@"marginT" defaultValue:5];
    CGFloat marginRR = [rBtnStyle floatValueForKey:@"marginR" defaultValue:8];
    NSString *titleColorR = [rBtnStyle stringValueForKey:@"color" defaultValue:@"#fff"];
    NSInteger titleFontR = [rBtnStyle integerValueForKey:@"size" defaultValue:14];
    if ([rBtnBg isKindOfClass:[NSString class]] && rBtnBg.length <= 0) {
        rBtnBg = @"0f0";
    }
    if ([titleColorR isKindOfClass:[NSString class]] && titleColorR.length <= 0) {
        titleColorR = @"fff";
    }
    
    NSInteger firstSelected = 0;
    //Init pickerView and add it to self
    if (!self.pickerView) {
        self.pickerView = [[UIMSPickerView alloc] initWithFrame:CGRectMake(0,titleHigh, self.bounds.size.width, self.bounds.size.height-titleHigh) withAllTitle:selectAll];
        self.pickerView.singleSelector = singleSelection;
        self.pickerView.itemStyle = itemStyle;
        self.pickerView.iconStyle = iconStyle;
        self.pickerView.selectedItems = self.selectedItems;
        self.pickerView.maxOfSelected = selectMax;
        for (id key in self.selectionStatesDic) {
            BOOL isSelected = [[self.selectionStatesDic objectForKey:key] boolValue];
            if (isSelected) {
                firstSelected = [key integerValue];
                break;
            }
        }
        [self.pickerView addAllMSPickerViewCells];
    }
    self.pickerView.delegate = self;
    [self addSubview:self.pickerView];
    //打开时滚动到第一个选中的row
    NSIndexPath *firstIPath = [NSIndexPath indexPathForRow:firstSelected inSection:0];
    [self.pickerView.internalTableView_ scrollToRowAtIndexPath:firstIPath atScrollPosition:UITableViewScrollPositionMiddle animated:YES];
    
    UIImageView *toolBar = [[UIImageView alloc]init];
    toolBar.frame = CGRectMake(0, 0, self.bounds.size.width, titleHigh);
    if ([UZAppUtils isValidColor:titleBg]) {
        toolBar.backgroundColor = [UZAppUtils colorFromNSString:titleBg];
    } else {
        titleBg = [self.multiPickerDelegate getRealPath:titleBg];
        toolBar.image = [UIImage imageWithContentsOfFile:titleBg];
    }
    [self addSubview:toolBar];
    [self bringSubviewToFront:toolBar];
    toolBar.userInteractionEnabled = YES;
    
    //leftBtn
    UIButton *leftBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    leftBtn.frame = CGRectMake(marginLL, marginTL, lBtnW, lBtnH);
    [leftBtn setTitle:lBtnTitle forState:UIControlStateNormal];
    leftBtn.titleLabel.font = [UIFont systemFontOfSize:titleFontL];
    [leftBtn setTitleColor:[UZAppUtils colorFromNSString:titleColorL] forState:UIControlStateNormal];
    if ([UZAppUtils isValidColor:lBtnBg]) {
        leftBtn.backgroundColor = [UZAppUtils colorFromNSString:lBtnBg];
    } else {
        lBtnBg = [self.multiPickerDelegate getRealPath:lBtnBg];
        [leftBtn setBackgroundImage:[UIImage imageWithContentsOfFile:lBtnBg] forState:UIControlStateNormal];
        leftBtn.backgroundColor = [UIColor clearColor];
    }
    [leftBtn addTarget:self action:@selector(leftButtonClick) forControlEvents:UIControlEventTouchUpInside];
    [toolBar addSubview:leftBtn];
    
    //rightButton
    UIButton *rightBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    rightBtn.frame = CGRectMake(self.bounds.size.width-marginRR-rBtnW, marginTR, rBtnW, rBtnH);
    //rightBtn.titleLabel.text = rBtnTitle;    //无效
    [rightBtn setTitle:rBtnTitle forState:UIControlStateNormal];
    rightBtn.titleLabel.font = [UIFont systemFontOfSize:titleFontR];
    [rightBtn setTitleColor:[UZAppUtils colorFromNSString:titleColorR] forState:UIControlStateNormal];
    if ([UZAppUtils isValidColor:rBtnBg]) {
        rightBtn.backgroundColor = [UZAppUtils colorFromNSString:rBtnBg];
    } else {
        rBtnBg = [self.multiPickerDelegate getRealPath:rBtnBg];
        [rightBtn setBackgroundImage:[UIImage imageWithContentsOfFile:rBtnBg] forState:UIControlStateNormal];
        rightBtn.backgroundColor = [UIColor clearColor];
    }
    [rightBtn addTarget:self action:@selector(confirmPickView) forControlEvents:UIControlEventTouchUpInside];
    [toolBar addSubview:rightBtn];
    
    CGFloat titleWid = self.bounds.size.width-marginLL-lBtnW-marginRR-rBtnW;
    CGRect titleFrame = CGRectMake(leftBtn.frame.origin.x+leftBtn.frame.size.width, 0, titleWid, titleHigh);
    UILabel *centerLabel = [[UILabel alloc]initWithFrame:titleFrame];
    centerLabel.text = centerTitle;
    centerLabel.textColor = [UZAppUtils colorFromNSString:titleColor];
    centerLabel.font = [UIFont systemFontOfSize:titleFont];
    centerLabel.textAlignment = NSTextAlignmentCenter;
    [toolBar addSubview:centerLabel];
}

- (void)initSelectionStatesDic {
    if (singleSelection) {
        //先将所有的置为 false
        for (NSInteger index = 0; index <self.entriesArray.count; index ++) {
            [self.selectionStatesDic setObject:[NSNumber numberWithBool:false] forKey:@(index)];
        }
        //检测最后一个status：selected/forever，并将其置为 true
        NSInteger key = -1;
        for (NSDictionary *keyDic in self.entriesArray) {
            NSString *status = [keyDic objectForKey:@"status"];
            if ([status isEqualToString:@"selected"] || [status isEqualToString:@"forever"]) {
                key = [self.entriesArray indexOfObject:keyDic];
            }
        }
        if (key >= 0) {
            [self.selectionStatesDic setObject:[NSNumber numberWithBool:true] forKey:@(key)];
        }
    } else if (selectMax > 0) {
        NSInteger key = 0, selectedNum = 0;
        for (NSDictionary *keyDic in self.entriesArray){
            NSString *status = [keyDic objectForKey:@"status"];
            BOOL isSelected = NO;
            if ([status isEqualToString:@"selected"] || [status isEqualToString:@"forever"]) {
                isSelected = YES;
                selectedNum ++;
            } else {
                isSelected = NO;
            }
            if (selectedNum > selectMax) {  //假如初始化时status数大于max值
                isSelected = NO;
            }
            [self.selectionStatesDic setObject:[NSNumber numberWithBool:isSelected] forKey:@(key)];
            key++;
        }
    } else {
        BOOL isAllSelected = YES;
        NSInteger key = 1;
        for (NSDictionary *keyDic in self.entriesArray){
            NSString *status = [keyDic objectForKey:@"status"];
            BOOL isSelected = NO;
            if ([status isEqualToString:@"selected"] || [status isEqualToString:@"forever"]) {
                isSelected = YES;
            } else {
                isSelected = NO;
                isAllSelected = NO;
            }
            [self.selectionStatesDic setObject:[NSNumber numberWithBool:isSelected] forKey:@(key)];
            key++;
        }
        if (isAllSelected) {
            [self.selectionStatesDic setObject:[NSNumber numberWithBool:YES] forKey:@(0)];
        } else {
            [self.selectionStatesDic setObject:[NSNumber numberWithBool:NO] forKey:@(0)];
        }
    }
}

#pragma mark - clickBtn -
- (NSMutableArray *)getSelecteditem {
    NSMutableArray *selectedEntriesArr = [NSMutableArray array];
    for (id row in [self.selectionStatesDic allKeys]) {
        if (selectMax == 0) {
            if ([row integerValue] == 0) {
                continue;
            }
            if ([[self.selectionStatesDic objectForKey:row] boolValue]) {
                NSDictionary *selectItem = [self.entriesArray objectAtIndex:([row integerValue]-1)];
                [selectItem setValue:@"selected" forKey:@"status"];
                [selectedEntriesArr addObject:selectItem];
            }
        } else {
            if ([[self.selectionStatesDic objectForKey:row] boolValue]) {
                NSDictionary *selectItem = [self.entriesArray objectAtIndex:([row integerValue])];
                [selectItem setValue:@"selected" forKey:@"status"];
                [selectedEntriesArr addObject:selectItem];
            }
        }
    }
    return selectedEntriesArr;
}

- (void)leftButtonClick {
    NSMutableArray *selectedEntriesArr = [self getSelecteditem];
    if ([self.multiPickerDelegate respondsToSelector:@selector(returnLeftBtnClickPickerString:)]) {
        [self.multiPickerDelegate returnLeftBtnClickPickerString:selectedEntriesArr];
    }
    [selectedEntriesArr removeAllObjects];
}

-(void)confirmPickView {
    NSMutableArray *selectedEntriesArr = [self getSelecteditem];
    if ([self.multiPickerDelegate respondsToSelector:@selector(returnRightBtnClickPickerString:)]) {
        [self.multiPickerDelegate returnRightBtnClickPickerString:selectedEntriesArr];
    }
    [selectedEntriesArr removeAllObjects];
}


#pragma mark -  UIMSPickerViewDelegate -
// Return the number of elements of your pickerview
-(NSInteger)numberOfRowsForPickerView:(UIMSPickerView *)pickerView {
    if (selectMax == 0) {
        return [self.entriesArray count]+1;
    } else {
        return [self.entriesArray count];
    }
}
// Return a plain UIString to display on the given row
- (NSString *)pickerView:(UIMSPickerView *)pickerView textForRow:(NSInteger)row {
    return [[self.entriesArray objectAtIndex:row] stringValueForKey:@"text" defaultValue:@""];
}
// Return a boolean selection state on the given row
- (BOOL)pickerView:(UIMSPickerView *)pickerView selectionStateForRow:(NSInteger)row {
    return [[self.selectionStatesDic objectForKey:@(row)] boolValue];
}

- (void)pickerView:(UIMSPickerView *)pickerView didCheckRow:(NSInteger)row {
	// Check whether all rows are checked or only one
    if ((row == 0) && (selectMax == 0)) {
        for (id key in [self.selectionStatesDic allKeys]) {
			[self.selectionStatesDic setObject:[NSNumber numberWithBool:YES] forKey:key];
        }
    } else {
		[self.selectionStatesDic setObject:[NSNumber numberWithBool:YES] forKey:@(row)];
    }
}

- (void)pickerView:(UIMSPickerView *)pickerView didUncheckRow:(NSInteger)row {
	// Check whether all rows are unchecked or only one
    if ((row == 0) && (selectMax == 0)) {
        for (id key in [self.selectionStatesDic allKeys]) {
			[self.selectionStatesDic setObject:[NSNumber numberWithBool:NO] forKey:key];
        }
    } else {
		[self.selectionStatesDic setObject:[NSNumber numberWithBool:NO] forKey:@(row)];
    }
}

- (void)pickerView:(UIMSPickerView *)pickerView didUncheckRowZero:(NSInteger)row {
    [self.selectionStatesDic setObject:[NSNumber numberWithBool:NO] forKey:@(0)];
}

#pragma mark - delegate -
- (void)returnMaxTip:(NSDictionary *)maxDict {
    if ([self.multiPickerDelegate respondsToSelector:@selector(returnMaxSelectedItem:)]) {
        [self.multiPickerDelegate returnMaxSelectedItem:maxDict];
    }
}

- (NSString *)getRealPath:(NSString *)paramPath {
    return [self.multiPickerDelegate getRealPath:paramPath];
}

- (void)returnSelectedItems {
    NSMutableArray *selectedEntriesArr = [self getSelecteditem];
    NSMutableDictionary * sendDict = [NSMutableDictionary dictionaryWithCapacity:2];
    [sendDict setObject:@"clickItem" forKey:@"eventType"];
    [sendDict setObject:selectedEntriesArr forKey:@"items"];
    if ([self.multiPickerDelegate respondsToSelector:@selector(returnMaxSelectedItem:)]) {
        [self.multiPickerDelegate returnMaxSelectedItem:sendDict];
    }
    [selectedEntriesArr removeAllObjects];
}

@end
