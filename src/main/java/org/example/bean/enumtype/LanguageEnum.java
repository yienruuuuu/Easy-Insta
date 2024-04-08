package org.example.bean.enumtype;

import lombok.Getter;

/**
 * @author Eric.Lee
 * Date: 2024/4/9
 */
@Getter
public enum LanguageEnum {
    ZH_TW("textZhTw"),
    EN("textEn"),
    JA("textJa"),
    RU("textRu"),
    OTHER("textEn");

    private final String description;

    LanguageEnum(String description) {
        this.description = description;
    }
}
