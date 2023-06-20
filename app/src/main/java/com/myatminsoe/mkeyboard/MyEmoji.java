package com.myatminsoe.mkeyboard;

public class MyEmoji {


    public static String get(int codePoint) {
        if (codePoint == -13) {
            return EmojiHolder.emojis[31];
        }
        return EmojiHolder.emojis[codePoint - 1];
    }
}
