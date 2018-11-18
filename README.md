# ImageSelection
结合微信和知乎的图片选择器

## 优点：
1. 可自定义主题色
2. 可同时选择拍照和图库照片（即拍一张照后 再选几张图库的图片）
3. 无需设置onActivityResult 通过回调获取图片
4. 单选时要可以裁剪



## 导入工程
`
compile 'com.luckyaf:imageselection:1.3.0'
`

## 使用：


    ImageSelection.getInstance()
                    .from(this)
                    .capture(true)
                    .needGif(true)
                    .crop(true)
                    .savePublic(true)
                    .translucent(true)
                    .maxSelectable(15)
                    .themeColor(Color.parseColor("#1E8AE8"))
                    .selectWord("确定")
                    .getImage(new ImageGetter() {
                         @Override
                         public void getImageSuccess(ImageData imageData) {
                                mTextView.setText(imageData.toString());
                                Toast.makeText(mContext,"size " + imageData.size(),Toast.LENGTH_SHORT).show();
                                ((ImageView)findViewById(R.id.img_select)).setImageURI(imageData.getImage());
                         }
                    });
 

## TODO:
- [x] 选中图片后的裁剪功能
- [x] 上传改项目至bintray



