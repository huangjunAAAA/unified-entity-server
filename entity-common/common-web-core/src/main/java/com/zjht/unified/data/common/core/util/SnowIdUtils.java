package com.zjht.unified.data.common.core.util;

import com.baomidou.mybatisplus.core.toolkit.Sequence;

public class SnowIdUtils {

    private static Sequence sequence = new Sequence();


    public static Long nextId() {
        return sequence.nextId();
    }


}
