/**
  * APICloud Modules
  * Copyright (c) 2014-2017 by APICloud, Inc. All Rights Reserved.
  * Licensed under the terms of the The MIT License (MIT).
  * Please see the license.html included with this distribution for details.
  */


#import <UIKit/UIKit.h>

@interface UIMSPickerViewCell : UITableViewCell
{
   @private
   BOOL selectionState_;
}

@property (nonatomic, assign) BOOL selectionState;
@property (nonatomic, copy) NSString *selectColor;
@property (nonatomic, copy) NSString *normalColor;
@property (nonatomic, assign) BOOL isDisable;
@property (nonatomic, assign) BOOL isForever;
@property (nonatomic, strong) UIImageView *cellImgView;
@property (nonatomic, strong) UILabel *cellLabel;

- (id)initWithReuseIdentifier:(NSString *)reuseIdentifier isImage:(BOOL)isImage withItemStyle:(NSDictionary *)itemStyle withIconStyle:(NSDictionary *)iconStyle;
@end
