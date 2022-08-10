package com.github.toastshaman.httprouter;

import com.github.toastshaman.httprouter.domain.MethodType;
import com.github.toastshaman.httprouter.domain.Pattern;

public interface Route {

    MethodType method();

    Pattern pattern();

    Handler handler();

    Route prefixWith(Pattern pattern);
}
