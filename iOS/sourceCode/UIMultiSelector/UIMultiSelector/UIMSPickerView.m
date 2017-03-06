/**
  * APICloud Modules
  * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
  * Licensed under the terms of the The MIT License (MIT).
  * Please see the license.html included with this distribution for details.
  */


#import "UIMSPickerView.h"
#import "UIMSPickerViewCell.h"
#import "UZAppUtils.h"
#import "NSDictionaryUtils.h"

#define isIOS8 ([[UIDevice currentDevice].systemVersion doubleValue] >= 8.0)

@interface UIMSPickerView ()
{
    int number;
    CGFloat itemHeight;
    NSString *_itemBg, *_itemActive, *_itemHighL;
    NSString *_titleBg, *_titleActive, *_titleHighL;
    NSString *_iconBg, *_iconActive, *_iconHighL;
    NSMutableDictionary *_allBtnDict;
    NSMutableArray *_disableArr, *_foreverArr;
    BOOL isLastForever;
}
@end

@implementation UIMSPickerView

@synthesize delegate = delegate_;
@synthesize allOptionTitle;
@synthesize bgImg;
@synthesize selectColor,normalColor;
@synthesize internalTableView_ ;

#pragma mark - NSObject stuff
- (id)initWithFrame:(CGRect)frame withAllTitle:(NSString *)allTitle {
	// Set fix width and height
	if ((self = [super initWithFrame:frame])) {
        number = 0;
        self.backgroundColor = [UIColor clearColor];
        self.clipsToBounds = YES;
        self.allOptionTitle = NSLocalizedString(allTitle, @"All option title");
        _allBtnDict = [[NSMutableDictionary alloc] init];
        _disableArr = [[NSMutableArray alloc]init];
        _foreverArr = [[NSMutableArray alloc]init];
     }
     return self;
}

-(void)addAllMSPickerViewCells {
    [self initItemAndIconStyle];
    NSString *colorLine = [self.itemStyle stringValueForKey:@"lineColor" defaultValue:@"#ccc"];
    if ([colorLine isKindOfClass:[NSString class]] && colorLine.length <= 0) {
        colorLine = @"#ccc";
    }
    internalTableView_ = [[UITableView alloc] initWithFrame:self.bounds];
    internalTableView_.delegate = self;
    internalTableView_.dataSource = self;
    internalTableView_.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    internalTableView_.separatorColor = [UZAppUtils colorFromNSString:colorLine];
    internalTableView_.showsVerticalScrollIndicator = NO;
    internalTableView_.backgroundColor = [UIColor whiteColor];
    internalTableView_.separatorInset = UIEdgeInsetsZero;
    if (isIOS8) {
        internalTableView_.layoutMargins = UIEdgeInsetsZero;
    }
    [self addSubview:internalTableView_];
}

- (void)dealloc {
	allOptionTitle = nil;
	if (bgImg) {
        self.bgImg = nil;
    }
	internalTableView_ = nil;
    self.selectColor = nil;
    self.normalColor = nil;
    self.selectedItems = nil;
     [_allBtnDict removeAllObjects];
     _allBtnDict = nil;
     [_disableArr removeAllObjects];
     _disableArr = nil;
     [_foreverArr removeAllObjects];
     _foreverArr = nil;
}

#pragma mark - UITableView
-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (allOptionTitle){
        number = (int)([delegate_ numberOfRowsForPickerView:self] ? [delegate_ numberOfRowsForPickerView:self]: 0);
    } else {
        number = (int)([delegate_ numberOfRowsForPickerView:self] ? [delegate_ numberOfRowsForPickerView:self]: 0);
    }
    return number;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return itemHeight;
}

- (UITableViewCell *)tableView:(UITableView *)aTableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *CellIdentifier = [NSString stringWithFormat:@"%d",(int)indexPath.row];
    CGFloat cellW = [self.iconStyle floatValueForKey:@"w" defaultValue:20];
    CGFloat cellH = [self.iconStyle floatValueForKey:@"h" defaultValue:cellW];
    CGFloat marginT = [self.iconStyle floatValueForKey:@"marginT" defaultValue:(itemHeight-cellH)/2.0];
    CGFloat marginH = [self.iconStyle floatValueForKey:@"marginH" defaultValue:8.0];
    NSString *alignIcon = [self.iconStyle stringValueForKey:@"align" defaultValue:@"left"];
    
    UIButton *cellBtn = [_allBtnDict objectForKey:[NSString stringWithFormat:@"%ld",(long)(1000+indexPath.row)]];
    UIMSPickerViewCell *cell = [aTableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UIMSPickerViewCell alloc] initWithReuseIdentifier:CellIdentifier isImage:self.iconStyle.count withItemStyle:self.itemStyle withIconStyle:self.iconStyle];
        cell.selectColor = _itemActive;
        cell.normalColor = _itemBg;
        //初始状态时cellBtn不存在，即创建
        if (cellBtn == nil) {
            cellBtn = [[UIButton alloc]init];
            if ([alignIcon isEqualToString:@"right"]) {
                [cellBtn setFrame:CGRectMake(self.bounds.size.width-marginH-cellW, marginT, cellW, cellH)];
            } else {
                [cellBtn setFrame:CGRectMake(marginH, marginT, cellW, cellH)];
            }
            cellBtn.tag = 1000 + indexPath.row;
            [cell.contentView addSubview:cellBtn];
            [_allBtnDict setObject:cellBtn forKey:[NSString stringWithFormat:@"%d",(int)cellBtn.tag]];
            [cellBtn setUserInteractionEnabled:NO];
        }
        isLastForever = [delegate_ pickerView:self selectionStateForRow:indexPath.row];
        if (indexPath.row > 1) {
            NSString *itemStatus = [[self.selectedItems objectAtIndex:(indexPath.row-1)] objectForKey:@"status"];
            if ([itemStatus isEqualToString:@"disable"]) {
                [_disableArr addObject:indexPath];
                cell.isDisable = YES;
            } else if (isLastForever && [itemStatus isEqualToString:@"forever"]) {
                [_foreverArr addObject:indexPath];
                cell.isForever = YES;
            } else {
                cell.isDisable = NO;
                cell.isForever = NO;
            }
        }
    }
    
    if (self.maxOfSelected == 0) {
        if (allOptionTitle && indexPath.row == 0) {
            cell.cellLabel.text = allOptionTitle;
            cell.cellLabel.textColor = [UZAppUtils colorFromNSString:_titleBg];
            BOOL allSelected = YES;
            for (int i = 0; i < [self.delegate numberOfRowsForPickerView:self]; i++) {
                if ([delegate_ pickerView:self selectionStateForRow:i] == NO) {
                    allSelected = NO;
                    break;
                }
            }
            cell.selectionState = allSelected;
        } else {
            int actualRow = (int)(indexPath.row);
            cell.cellLabel.text = [delegate_ pickerView:self textForRow:actualRow-1];  // >0,否则溢出
            cell.selectionState = [delegate_ pickerView:self selectionStateForRow:actualRow];
        }
    } else {
        int actualRow = (int)(indexPath.row);
        cell.cellLabel.text = [delegate_ pickerView:self textForRow:actualRow];
        cell.selectionState = [delegate_ pickerView:self selectionStateForRow:actualRow];
    }

    if (cell.selectionState) {
        cellBtn.selected = YES;
        [self setTableCellBackgroundColorOrImage:_itemActive forCell:cell];
        [self setActiveBackgroundColorOrImage:_iconActive forButton:cellBtn];
    } else {
        cellBtn.selected = NO;
        [self setTableCellBackgroundColorOrImage:_itemBg forCell:cell];
        [self setNormalBackgroundColorOrImage:_iconBg forButton:cellBtn];
    }
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.separatorInset = UIEdgeInsetsZero;
    if (isIOS8) {
        cell.layoutMargins = UIEdgeInsetsZero;
    }
    return cell;
}

-(NSIndexPath *)tableView:(UITableView *)tableView willSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    UIMSPickerViewCell *currCell = [tableView cellForRowAtIndexPath:indexPath];
    if (self.singleSelector) {
        if (isLastForever) {
            return indexPath;
        } else if (currCell.isDisable) {
            [self tableView:tableView didUnhighlightRowAtIndexPath:indexPath];
            return nil;
        } else {
            return indexPath;
        }
    }

    if (currCell.isForever) {
        [self tableView:tableView didUnhighlightRowAtIndexPath:indexPath];
        return nil;
    } else if (currCell.isDisable) {
        [self tableView:tableView didUnhighlightRowAtIndexPath:indexPath];
        return nil;
    }
    int btnNum = 0;
    for (UIButton *btn in [_allBtnDict allValues]) {
        if ((btn.tag == 1000+indexPath.row) && (self.maxOfSelected == 0)) {
            continue;
        } else {
            if (btn.selected) {
                btnNum++;
            }
        }
    }
    if (!currCell.selectionState) {   //如果此时是选中状态则即使超过max也是可以点选中的
        BOOL isMax = [self maxOfItem:btnNum];
        if (isMax) {
            [self tableView:tableView didUnhighlightRowAtIndexPath:indexPath];
            return nil;
        } else {
            return indexPath;
        }
    } else {
        return indexPath;
    }
}

 - (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    UIMSPickerViewCell *curMSCell = (UIMSPickerViewCell *)[tableView cellForRowAtIndexPath:indexPath];
    UIButton *btn = (UIButton *)[tableView viewWithTag:1000+indexPath.row];
     
    curMSCell.selectionState = !curMSCell.selectionState;
    //判断若为all键。则操作所有的button, //max > 0,不显示全选项，那么row:0 跟其他项一样，无特殊
    if ((indexPath.row == 0) && (self.maxOfSelected == 0)) {
        if (!curMSCell.selectionState) {
            for (UIButton *btn in [_allBtnDict allValues]) {
                if (btn.tag == 1000) {
                    continue;
                }
                //是否是forever（不可取消状态）
                if ([_foreverArr containsObject:[NSIndexPath indexPathForRow:btn.tag-1000 inSection:0]]) {
                    btn.selected = YES;
                    [self setNormalBackgroundColorOrImage:_iconActive forButton:btn];
                } else {
                    btn.selected = NO;
                    [self setNormalBackgroundColorOrImage:_iconBg forButton:btn];
                }
            }
        } else {
            for (UIButton *btn in [_allBtnDict allValues]) {
                if (btn.tag == 1000) {
                    continue;
                }
                //是否是disable（不可选中）状态
                if ([_disableArr containsObject:[NSIndexPath indexPathForRow:btn.tag-1000 inSection:0]]) {
                    btn.selected = NO;
                    [self setNormalBackgroundColorOrImage:_iconBg forButton:btn];
                } else {
                    btn.selected = YES;
                    [self setActiveBackgroundColorOrImage:_iconActive forButton:btn];
                }
            }
        }
    } else {
        if (!curMSCell.selectionState) {
            btn.selected = NO;
            [self setNormalBackgroundColorOrImage:_iconBg forButton:btn];
        } else {
            btn.selected = YES;
            [self setActiveBackgroundColorOrImage:_iconActive forButton:btn];
            //如果是singleSelector模式，将其他项重置为false
            if (self.singleSelector) {
                for (UIButton *otherBtn in [_allBtnDict allValues]) {
                    if (otherBtn.tag == btn.tag) {
                        continue;
                    }
                    otherBtn.selected = NO;
                    [self setNormalBackgroundColorOrImage:_iconBg forButton:otherBtn];
                }
                //单独全部重置：初始化选中项为“非visibleCells”时，_allBtnDict中也不存在此索引对应的项
                for (NSInteger cellIndex=0; cellIndex<number; cellIndex++) {
                    if (cellIndex == (btn.tag-1000)) {
                        continue;
                    }
                    [delegate_ pickerView:self didUncheckRow:cellIndex];
                }
            }
        }
    }
    
    if (self.maxOfSelected == 0) {
        int btnNum = 0;
        for (UIButton *btn in [_allBtnDict allValues]) {
            if (btn.tag == 1000) {
                 continue;
            } else {
                if (btn.selected) {
                     btnNum++;
                }
             }
         }
         //如果非全选项全部都被选中，则全选项也显示选中状态
         UIButton *allButton = (UIButton *)[tableView viewWithTag:1000];
     
         /* returns nil if cell is not visible or index path is out of range,所以如果在最后一行来获取第一行的cell 是不可能的，得到的结果为nil.
          UIMSPickerViewCell *oneCell = [tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:indexPath.section]];
          */
        if (btnNum == _allBtnDict.count-1) {
            allButton.selected = YES;
            [self setActiveBackgroundColorOrImage:_iconActive forButton:allButton];
            if ([self.delegate respondsToSelector:@selector(pickerView:didCheckRow:)]){
                [delegate_ pickerView:self didCheckRow:0];
            }
        } else {
            allButton.selected = NO;
            [self setNormalBackgroundColorOrImage:_iconBg forButton:allButton];
            if ([self.delegate respondsToSelector:@selector(pickerView:didUncheckRowZero:)]){
                [delegate_ pickerView:self didUncheckRowZero:0];
            }
        }
    } else {
        //max >0
    }
    
 
    // Inform delegate
    int actualRow = (int)indexPath.row;
    if (curMSCell.selectionState == YES) {
        if ([self.delegate respondsToSelector:@selector(pickerView:didCheckRow:)]){
            [delegate_ pickerView:self didCheckRow:actualRow];
        }
    } else {
        if ([self.delegate respondsToSelector:@selector(pickerView:didUncheckRow:)]) {
            [delegate_ pickerView:self didUncheckRow:actualRow];
        }
    }

    for (UIMSPickerViewCell *currCell in tableView.visibleCells) {
        NSInteger iterateRow = [tableView indexPathForCell:currCell].row;
        if (allOptionTitle && iterateRow == 0) {
            if (self.maxOfSelected > 0) {  //max >0
                currCell.selectionState = [delegate_ pickerView:self selectionStateForRow:iterateRow];
                if (currCell.selectionState) {
                    [self setTableCellBackgroundColorOrImage:_itemActive forCell:currCell];
                } else {
                    [self setTableCellBackgroundColorOrImage:_itemBg forCell:currCell];
                }
            } else {
                BOOL allSelected = YES;
                for (int i = 1; i < [self.delegate numberOfRowsForPickerView:self]; i++) {
                    if ([delegate_ pickerView:self selectionStateForRow:i] == NO) {
                        allSelected = NO;
                        break;
                    }
                }
                currCell.selectionState = allSelected;
                if (currCell.selectionState) {
                    [self setTableCellBackgroundColorOrImage:_itemActive forCell:currCell];
                } else {
                    [self setTableCellBackgroundColorOrImage:_itemBg forCell:currCell];
                }
            }
        } else if (iterateRow > 0 && iterateRow < [delegate_ numberOfRowsForPickerView:self]) {
            currCell.selectionState = [delegate_ pickerView:self selectionStateForRow:iterateRow];
            if (currCell.selectionState) {
                [self setTableCellBackgroundColorOrImage:_itemActive forCell:currCell];
            } else {
                [self setTableCellBackgroundColorOrImage:_itemBg forCell:currCell];
            }
        }
    }
    [self.delegate returnSelectedItems];
}

- (void)tableView:(UITableView *)tableView didHighlightRowAtIndexPath:(NSIndexPath *)indexPath {
    UIMSPickerViewCell *cell = (UIMSPickerViewCell *)[tableView cellForRowAtIndexPath:indexPath];
    cell.cellLabel.textColor = [UZAppUtils colorFromNSString:_titleHighL];
    if ([UZAppUtils isValidColor: _itemHighL]) {
        cell.backgroundColor = [UZAppUtils colorFromNSString:_itemHighL];
    } else {
        cell.backgroundColor = [UIColor clearColor];
        _itemHighL = [self.delegate getRealPath:_itemHighL];
        cell.cellImgView.image = [UIImage imageWithContentsOfFile:_itemHighL];
    }
}

- (void)tableView:(UITableView *)tableView didUnhighlightRowAtIndexPath:(NSIndexPath *)indexPath {
    UIMSPickerViewCell *cell = (UIMSPickerViewCell *)[tableView cellForRowAtIndexPath:indexPath];
    if (cell.selectionState) {
        [self setTableCellBackgroundColorOrImage:_itemActive forCell:cell];
    } else {
        [self setTableCellBackgroundColorOrImage:_itemBg forCell:cell];
    }
}


#pragma mark - ScrollView
 - (void)scrollViewDidEndDecelerating:(UITableView *)tableView {
  int co = ((int)tableView.contentOffset.y % (int)tableView.rowHeight);
  if (co < tableView.rowHeight / 2)
    [tableView setContentOffset:CGPointMake(0, tableView.contentOffset.y - co) animated:YES];
  else
    [tableView setContentOffset:CGPointMake(0, tableView.contentOffset.y + (tableView.rowHeight - co)) animated:YES];
}

- (void)scrollViewDidEndDragging:(UITableView *)scrollView willDecelerate:(BOOL)decelerate {
    if(decelerate) {
        return;
    }
    [self scrollViewDidEndDecelerating:scrollView];
}

#pragma mark - Utils -
- (void)initItemAndIconStyle {
    itemHeight = [self.itemStyle floatValueForKey:@"h" defaultValue:35.0];
    
    _itemBg = [self.itemStyle stringValueForKey:@"bg" defaultValue:@"#fff"];
    _itemActive = [self.itemStyle stringValueForKey:@"bgActive" defaultValue:_itemBg];
    _itemHighL = [self.itemStyle stringValueForKey:@"bgHighlight" defaultValue:_itemBg];
    if ([_itemBg isKindOfClass:[NSString class]] && _itemBg.length <= 0) {
        _itemBg = @"#fff";
    }
    if ([_itemActive isKindOfClass:[NSString class]] && _itemActive.length <= 0) {
        _itemActive = _itemBg;
    }
    if ([_itemHighL isKindOfClass:[NSString class]] && _itemHighL.length <= 0) {
        _itemHighL = _itemBg;
    }

    _titleBg = [self.itemStyle stringValueForKey:@"color" defaultValue:@"#444"];
    _titleActive = [self.itemStyle stringValueForKey:@"active" defaultValue:_titleBg];
    _titleHighL = [self.itemStyle stringValueForKey:@"highlight" defaultValue:_titleBg];
    if ([_titleBg isKindOfClass:[NSString class]] && _titleBg.length <= 0) {
        _titleBg = @"#444";
    }
    if ([_titleActive isKindOfClass:[NSString class]] && _titleActive.length <= 0) {
        _titleActive = _titleBg;
    }
    if ([_titleHighL isKindOfClass:[NSString class]] && _titleHighL.length <= 0) {
        _titleHighL = _titleBg;
    }
    
    _iconBg = [self.iconStyle stringValueForKey:@"bg" defaultValue:@"rgba(0,0,0,0)"];
    _iconActive = [self.iconStyle stringValueForKey:@"bgActive" defaultValue:_iconBg];
    _iconHighL = [self.iconStyle stringValueForKey:@"bgHighlight" defaultValue:_iconBg];
    if ([_iconBg isKindOfClass:[NSString class]] && _iconBg.length <= 0) {
        _iconBg = @"rgba(0,0,0,0)";
    }
    if ([_iconActive isKindOfClass:[NSString class]] && _iconActive.length <= 0) {
        _iconActive = _iconBg;
    }
    if ([_iconHighL isKindOfClass:[NSString class]] && _iconHighL.length <= 0) {
        _iconHighL = _iconBg;
    }
}

- (void)setNormalBackgroundColorOrImage:(NSString *)bgPath forButton:(UIButton *)btn {
    if ([UZAppUtils isValidColor:bgPath]) {
        btn.backgroundColor = [UZAppUtils colorFromNSString:bgPath];
    } else {
        [btn setBackgroundImage:[UIImage imageWithContentsOfFile:[self.delegate getRealPath:bgPath]] forState:UIControlStateNormal];        //normal
        btn.backgroundColor = [UIColor clearColor];
    }
}

- (void)setActiveBackgroundColorOrImage:(NSString *)activePath forButton:(UIButton *)btn {
    if ([UZAppUtils isValidColor:activePath]) {
        btn.backgroundColor = [UZAppUtils colorFromNSString:activePath];
    } else {
        [btn setBackgroundImage:[UIImage imageWithContentsOfFile:[self.delegate getRealPath:activePath]] forState:UIControlStateSelected];      //active
        btn.backgroundColor = [UIColor clearColor];
    }
}

- (void)setTableCellBackgroundColorOrImage:(NSString *)colorPath forCell:(UIMSPickerViewCell *)curCell {
    if ([UZAppUtils isValidColor:colorPath]) {
        curCell.backgroundColor = [UZAppUtils colorFromNSString:colorPath];
        curCell.cellImgView.image = nil;
    } else {
        curCell.cellImgView.image = [UIImage imageWithContentsOfFile:[self.delegate getRealPath:colorPath]];
        curCell.backgroundColor = [UIColor clearColor];
    }
    if (curCell.selectionState) {
        curCell.cellLabel.textColor = [UZAppUtils colorFromNSString:_titleActive];
    } else {
        curCell.cellLabel.textColor = [UZAppUtils colorFromNSString:_titleBg];
    }
}

- (BOOL)maxOfItem:(NSInteger )selectedNum {
    NSInteger selectedMax = 0;
    if (self.maxOfSelected == 0) {
        selectedMax = number;
    } else {
        selectedMax = self.maxOfSelected;
    }
    if (selectedNum >= selectedMax){
        NSDictionary *maxTip = [NSDictionary dictionaryWithObject:@"overflow" forKey:@"eventType"];
        if ([self.delegate respondsToSelector:@selector(returnMaxTip:)]) {
            [self.delegate returnMaxTip:maxTip];
        }
        return true;
    } else {
        return false;
    }
}

@end
