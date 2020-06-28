//
// Created by huangjy on 2018/12/25.
//

#ifndef NATIVE_JS_ANDROID_VALUEMANAGER_H
#define NATIVE_JS_ANDROID_VALUEMANAGER_H

#include <JavaScriptCore/JavaScript.h>
#include <JavaInterface/JavaScriptCore.h>
#include <map>

class ValueManager {
private:
    std::map<std::string, jobject> m_values;
public:
    ~ValueManager();
    /**
     * store java jsvalue
     * @param valueRef : java jsvalue
     */
    void storeJSValue(jobject jsValue);

    /**
     * remove java jsvalue
     * @param valueRef : java jsvalue
     */
    void removeJSValue(jobject jsValue);
};


#endif //NATIVE_JS_ANDROID_VALUEMANAGER_H
