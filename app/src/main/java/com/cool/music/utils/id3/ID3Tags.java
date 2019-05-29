package com.cool.music.utils.id3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.io.TextEncoding;
import org.blinkenlights.jid3.v2.APICID3V2Frame;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ID3Tags {
    private static final String FRONT_COVER_DESC = "front_cover";
    private static final String MIME_TYPE_JPEG = "image/jpeg";

    // 标题
    private String title;
    // 艺术家
    private String artist;
    // 专辑
    private String album;
    // 流派
    private String genre;
    // 年份
    private int year;
    // 注释
    private String comment;
    // 封面图片
    private Bitmap coverBitmap;

    public void fillID3Tag(ID3V2_3_0Tag id3V2_3_0Tag) throws ID3Exception {
        TextEncoding.setDefaultTextEncoding(TextEncoding.UNICODE);
        if (title != null) {
            id3V2_3_0Tag.setTitle(title);
        }
        if (artist != null) {
            id3V2_3_0Tag.setArtist(artist);
        }
        if (album != null) {
            id3V2_3_0Tag.setAlbum(album);
        }
        if (genre != null) {
            id3V2_3_0Tag.setGenre(genre);
        }
        if (year > 0 && year <= 9999) {
            id3V2_3_0Tag.setYear(year);
        }
        if (comment != null) {
            id3V2_3_0Tag.setComment(comment);
        }

        TextEncoding.setDefaultTextEncoding(TextEncoding.ISO_8859_1);
        /**
         * 在Android2.3时代，Bitmap的引用是放在堆中的，而Bitmap的数据部分是放在栈中的，需要用户调用recycle
         * 方法手动进行内存回收，而在Android2.3之后，整个Bitmap，包括数据和引用，都放在了堆中，这样，
         * 整个Bitmap的回收就全部交给GC了。即使在Android2.3之后的版本中去调用recycle，系统也是会强制回收内存。
         * coverBitmap != null 判断的是引用是否为空，而!coverBitmap.isRecycled()判断的是引用指向的数据是否被回收。
         */
        if (coverBitmap != null && !coverBitmap.isRecycled()) {
            byte[] data = bitmapToBytes(coverBitmap);
            if (data != null) {
                id3V2_3_0Tag.removeAPICFrame(FRONT_COVER_DESC);//APIC这个标识表示专辑图片
                //APIC标识的东西包含帧标识头和帧标识内容组成
                APICID3V2Frame apicid3V2Frame = new APICID3V2Frame(MIME_TYPE_JPEG, APICID3V2Frame.PictureType.FrontCover, FRONT_COVER_DESC, data);
                id3V2_3_0Tag.addAPICFrame(apicid3V2Frame);
            }
        }
    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**<a href="https://coolxing.iteye.com/blog/1446760">使用场景</a>*/
    public static class Builder {
        private ID3Tags id3Tags;

        public Builder() {
            id3Tags = new ID3Tags();
        }

        public Builder setTitle(String title) {
            id3Tags.title = title;
            return this;
        }

        public Builder setArtist(String artist) {
            id3Tags.artist = artist;
            return this;
        }

        public Builder setAlbum(String album) {
            id3Tags.album = album;
            return this;
        }

        public Builder setGenre(String genre) {
            id3Tags.genre = genre;
            return this;
        }

        public Builder setYear(int year) {
            id3Tags.year = year;
            return this;
        }

        public Builder setComment(String comment) {
            id3Tags.comment = comment;
            return this;
        }

        public Builder setCoverFile(File coverFile) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap coverBitmap = BitmapFactory.decodeFile(coverFile.getPath(), options);
            return setCoverBitmap(coverBitmap);
        }

        public Builder setCoverBitmap(Bitmap coverBitmap) {
            id3Tags.coverBitmap = coverBitmap;
            return this;
        }

        public ID3Tags build() { return id3Tags;}
    }
}
