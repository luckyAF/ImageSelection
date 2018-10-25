# ImageSelection
结合微信和知乎的图片选择器

## 优点：
1. 可自定义主题色
2. 可同时选择拍照和图库照片（即拍一张照后 再选几张图库的图片）
3. 无需设置onActivityResult 通过回调获取图片

## 导入工程
`
compile 'com.luckyaf:imageselection:1.2.3'
`

## 使用：


    ImageSelection.getInstance()
                    .from(this)
                    .capture(true)
                    .needGif(true)
                    .savePublic(true)
                    .translucent(true)
                    .maxSelectable(15)
                    .themeColor(Color.parseColor("#1E8AE8"))
                    .selectWord("发送")
                    .getImage(new SelectionCreator.ImageGette() {
                        @Override
                        public void getImageSuccess(ImageData imageData) { 
                            Toast.makeText(mContext,"size” + imageData.size(),Toast.LENGTH_SHORT).show();    
                            }
                        })
                    .start();
 

## TODO:
- [ ] 选中图片后的编辑功能
- [x] 上传改项目至bintray

