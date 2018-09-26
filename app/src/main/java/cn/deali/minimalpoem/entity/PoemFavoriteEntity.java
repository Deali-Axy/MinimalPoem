package cn.deali.minimalpoem.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PoemFavoriteEntity {
    @Id
    private long id;
    private String title;
    private String author;
    private String content;


    @Keep
    public PoemFavoriteEntity(long id, String title, String author, String content) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
    }

    @Keep
    public PoemFavoriteEntity(String title, String author, String content) {
        this.title = title;
        this.author = author;
        this.content = content;
    }


    @Generated(hash = 1982634913)
    public PoemFavoriteEntity() {
    }


    public long getId() {
        return this.id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getTitle() {
        return this.title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getAuthor() {
        return this.author;
    }


    public void setAuthor(String author) {
        this.author = author;
    }


    public String getContent() {
        return this.content;
    }


    public void setContent(String content) {
        this.content = content;
    }
}
