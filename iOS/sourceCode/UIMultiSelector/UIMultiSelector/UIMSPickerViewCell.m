/**
  * APICloud Modules
  * Copyright (c) 2014-2017 by APICloud, Inc. All Rights Reserved.
  * Licensed under the terms of the The MIT License (MIT).
  * Please see the license.html included with this distribution for details.
  */

#import "UIMSPickerViewCell.h"
#import "NSDictionaryUtils.h"
#import "UZAppUtils.h"

@interface UIMSPickerViewCell ()
{
    CGFloat cellW, marginH;
    NSString *_iconAlign;
    NSString *_titleBg, *_titleActive, *_titleHighL;
}
@end

@implementation UIMSPickerViewCell

@synthesize selectColor,normalColor;

 - (id)initWithReuseIdentifier:(NSString *)reuseIdentifier isImage:(BOOL)isImage withItemStyle:(NSDictionary *)itemStyle withIconStyle:(NSDictionary *)iconStyle {
    if ((self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentifier])) {
        selectionState_ = NO;
        
        NSInteger fontSize = [itemStyle integerValueForKey:@"size" defaultValue:14];
        NSString *textAlign = [itemStyle stringValueForKey:@"textAlign" defaultValue:@"left"];
        
        cellW = [iconStyle floatValueForKey:@"w" defaultValue:20];
        marginH = [iconStyle floatValueForKey:@"marginH" defaultValue:8.0];
        _iconAlign = [iconStyle stringValueForKey:@"align" defaultValue:@"left"];

        _titleBg = [itemStyle stringValueForKey:@"color" defaultValue:@"#444"];
        _titleActive = [itemStyle stringValueForKey:@"active" defaultValue:_titleBg];
        _titleHighL = [itemStyle stringValueForKey:@"highlight" defaultValue:_titleBg];
        if ([_titleBg isKindOfClass:[NSString class]] && _titleBg.length <= 0) {
            _titleBg = @"#444";
        }
        if ([_titleActive isKindOfClass:[NSString class]] && _titleActive.length <= 0) {
            _titleActive = _titleBg;
        }
        if ([_titleHighL isKindOfClass:[NSString class]] && _titleHighL.length <= 0) {
            _titleHighL = _titleBg;
        }
        self.cellImgView = [[UIImageView alloc]initWithFrame:self.bounds];
        self.cellLabel = [[UILabel alloc]init];
        if ([_iconAlign isEqualToString:@"right"]) {
            self.cellLabel.frame = CGRectMake(0, 0, self.bounds.size.width-cellW-marginH, self.bounds.size.height);
        } else {
            self.cellLabel.frame = CGRectMake(cellW+marginH, 0, self.bounds.size.width-cellW-marginH, self.bounds.size.height);
        }
        self.cellLabel.font = [UIFont systemFontOfSize:fontSize];
        if ([textAlign isEqualToString:@"center"]) {
            self.cellLabel.textAlignment = NSTextAlignmentCenter;
        } else if ([textAlign isEqualToString:@"right"]) {
            self.cellLabel.textAlignment = NSTextAlignmentRight;
        } else {
            self.cellLabel.textAlignment = NSTextAlignmentLeft;
        }
        //放弃使用UITableviewCell 自带的imageView、textLabel，不好控制，当imageView使用图片时image将覆盖textLabel.
        [self.contentView addSubview:self.cellImgView];
        [self.contentView addSubview:self.cellLabel];
    }
    return self;
}

- (BOOL)selectionState {
    if (self.isDisable) {
        return NO;
    } else if (self.isForever) {
        return YES;
    } else {
        return selectionState_;
    }
}

- (void)setSelectionState:(BOOL)selectionState {
    selectionState_ = selectionState;
    if (selectionState_ == YES) {
        if ([UZAppUtils isValidColor:self.selectColor]) {
            self.backgroundColor = [UZAppUtils colorFromNSString:self.selectColor];
            self.cellImgView.image = nil;
        } else {
            self.backgroundColor = [UIColor clearColor];
            self.cellImgView.image = [UIImage imageWithContentsOfFile:self.selectColor];
        }
        self.cellLabel.textColor = [UZAppUtils colorFromNSString:_titleActive];
    } else {
        if ([UZAppUtils isValidColor:self.normalColor]) {
            self.backgroundColor = [UZAppUtils colorFromNSString:self.normalColor];
            self.cellImgView.image = nil;
        } else {
            self.backgroundColor = [UIColor clearColor];
            self.cellImgView.image = [UIImage imageWithContentsOfFile:self.normalColor];
        }
        self.cellLabel.textColor = [UZAppUtils colorFromNSString:_titleBg];
    }
}

- (void)layoutSubviews {
    [super layoutSubviews];
     self.cellImgView.frame = self.bounds;
     if ([_iconAlign isEqualToString:@"right"]) {
         self.cellLabel.frame = CGRectMake(0, 0, self.bounds.size.width-cellW-marginH, self.bounds.size.height);
     } else {
         self.cellLabel.frame = CGRectMake(cellW+marginH, 0, self.bounds.size.width-cellW-marginH, self.bounds.size.height);
     }
}

@end
