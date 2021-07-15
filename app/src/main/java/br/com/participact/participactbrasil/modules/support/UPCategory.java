package br.com.participact.participactbrasil.modules.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bergmannsoft.util.FileCache;
import com.bergmannsoft.util.ImageUtils;
import com.bergmannsoft.util.Utils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;

public class UPCategory implements Serializable {

    private Long id;
    private String name;
    private String color;
    @SerializedName("urlAsset")
    private String iconUrl;
    @SerializedName("urlIcon")
    private String pinUrl;
    @SerializedName("map")
    private List<UPCategory> subcategories;
    private Long parentId;

    public void init() {
        if (subcategories != null) {
            for (UPCategory subcategory : subcategories) {
                subcategory.parentId = id;
            }
        }
        if (color != null || color.toUpperCase().equals("#FFFFFF")) {
            color = "#7E986C";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public List<UPCategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<UPCategory> subcategories) {
        this.subcategories = subcategories;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

//    public int pin() {
//        if (name != null) {
//            switch (name.toUpperCase()) {
//                case "ACESSIBILIDADE":
//                    return R.mipmap.pin_acessibilidade;
//                case "CONVIVÊNCIA":
//                    return R.mipmap.pin_convivencia;
//                case "INFRAESTRUTURA":
//                    return R.mipmap.pin_infra;
//                case "MEIO AMBIENTE":
//                    return R.mipmap.pin_meio_ambiente;
//                case "MOBILIDADE URBANA":
//                    return R.mipmap.pin_mobilidade;
//                case "OUTROS":
//                    return R.mipmap.pin_outros;
//                case "POLUIÇÃO":
//                    return R.mipmap.pin_poluicao;
//                case "PRAIA":
//                    return R.mipmap.pin_praia;
//                case "SEGURANÇA":
//                    return R.mipmap.pin_seguranca;
//                case "SOCIAL":
//                    return R.mipmap.pin_social;
//                case "TRANSPORTE PUBLICO":
//                case "TRANSPORTE PÚBLICO":
//                    return R.mipmap.pin_transporte;
//                case "ZOONOSES E ANIMAIS":
//                    return R.mipmap.pin_zoonoses;
//            }
//        }
//        return R.mipmap.pin_outros;
//    }

    private String iconKey() {
        if (iconUrl != null) {
            int index = iconUrl.lastIndexOf("/");
            if (index > 0) {
                String key = iconUrl.substring(index + 1).replaceAll("\\.", "_").replaceAll("_", "").replace("png", "").replace("jpg", "").toLowerCase();
                return key;
            }
        }
        return "category_icon" + getId();
    }

    public Bitmap icon(final FileCache.OnBitmapDownloadedListener listener) {
        Bitmap bmp = App.getInstance().getFileCache().getBitmap(iconKey());
        if (bmp == null) {
            App.getInstance().getFileCache().downloadAsync(iconKey(), iconUrl, new FileCache.FileCacheCallback() {
                @Override
                public void onDownloadDone(String key, Bitmap bmp) {
                    listener.onDownloaded(bmp);
                }
            });
        }
        return bmp;
    }

    private String pinKey() {
        if (pinUrl != null) {
            int index = pinUrl.lastIndexOf("/");
            if (index > 0) {
                String key = pinUrl.substring(index + 1).replaceAll("\\.", "_").replaceAll("_", "").replace("png", "").replace("jpg", "").toLowerCase();
                return key;
            }
        }
        return "category_pin" + getId();
    }

    public Bitmap pin(final FileCache.OnBitmapDownloadedListener listener) {
        if (pinUrl != null) {
            Bitmap bmp = App.getInstance().getFileCache().getBitmap(pinKey());
            if (bmp == null) {
                App.getInstance().getFileCache().downloadAsync(pinKey(), pinUrl, new FileCache.FileCacheCallback() {
                    @Override
                    public void onDownloadDone(String key, Bitmap bmp) {
                        if (bmp == null) {
                            listener.onDownloaded(BitmapFactory.decodeResource(App.getInstance().getResources(), R.mipmap.pin_outros));
                        } else {
                            Bitmap resized = ImageUtils.scale(bmp, (int)(((double)bmp.getWidth()) * 0.7));
                            App.getInstance().getFileCache().remove(pinKey());
                            App.getInstance().getFileCache().put(pinKey(), resized);
                            listener.onDownloaded(resized);
                        }
                    }
                });
            }
            return bmp;
        }
        return BitmapFactory.decodeResource(App.getInstance().getResources(), R.mipmap.pin_outros);
    }

}
