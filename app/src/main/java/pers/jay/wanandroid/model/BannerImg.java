package pers.jay.wanandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BannerImg implements Parcelable {

        /**
         * desc : Android高级进阶直播课免费学习
         * id : 24
         * imagePath : https://www.wanandroid.com/blogimgs/bb4937de-b6f3-4c7e-b7d0-66d02f54abee.jpeg
         * isVisible : 1
         * order : 10
         * title : Android高级进阶直播课免费学习
         * type : 0
         * url : https://url.163.com/4bj
         */

        private String desc;
        private int id;
        private String imagePath;
        private int isVisible;
        private int order;
        private String title;
        private int type;
        private String url;

        public String getDesc() { return desc;}

        public void setDesc(String desc) { this.desc = desc;}

        public int getId() { return id;}

        public void setId(int id) { this.id = id;}

        public String getImagePath() { return imagePath;}

        public void setImagePath(String imagePath) { this.imagePath = imagePath;}

        public int getIsVisible() { return isVisible;}

        public void setIsVisible(int isVisible) { this.isVisible = isVisible;}

        public int getOrder() { return order;}

        public void setOrder(int order) { this.order = order;}

        public String getTitle() { return title;}

        public void setTitle(String title) { this.title = title;}

        public int getType() { return type;}

        public void setType(int type) { this.type = type;}

        public String getUrl() { return url;}

        public void setUrl(String url) { this.url = url;}

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.desc);
                dest.writeInt(this.id);
                dest.writeString(this.imagePath);
                dest.writeInt(this.isVisible);
                dest.writeInt(this.order);
                dest.writeString(this.title);
                dest.writeInt(this.type);
                dest.writeString(this.url);
        }

        public BannerImg() {}

        protected BannerImg(Parcel in) {
                this.desc = in.readString();
                this.id = in.readInt();
                this.imagePath = in.readString();
                this.isVisible = in.readInt();
                this.order = in.readInt();
                this.title = in.readString();
                this.type = in.readInt();
                this.url = in.readString();
        }

        public static final Parcelable.Creator<BannerImg> CREATOR = new Parcelable.Creator<BannerImg>() {
                @Override
                public BannerImg createFromParcel(Parcel source) {return new BannerImg(source);}

                @Override
                public BannerImg[] newArray(int size) {return new BannerImg[size];}
        };
}
