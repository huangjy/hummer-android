//
// Created by huangjy on 2018/12/24.
//

#ifndef NATIVE_JS_ANDROID_CONTEXTMANAGER_H
#define NATIVE_JS_ANDROID_CONTEXTMANAGER_H


#include <JavaScriptCore/JavaScript.h>
#include <map>
#include "ContextWrapper.h"
#include "ValueManager.h"
#include <vector>

class ContextManager {

private:
    std::map<std::string, jobject> m_ctxDict;
    std::map<std::string, ValueManager *> m_valueDict;
    std::vector <jobject> m_clsObjects;
    ContextManager(){};
    ContextManager(ContextManager const&);
    ContextManager& operator=(ContextManager const&);
    ~ContextManager(){};
public:
    static ContextManager& manager() {
        static ContextManager sManager;
        return sManager;
    }

    /**
     * store java jscontext
     * @param jsContext : java jscontext
     */
    void storeJSContext(jobject jsContext);

    /**
     * remove java jscontext
     * @param jsContext : java jscontext
     */
    void removeJSContext(jobject jsContext);

    /**
     * get java jscontext
     * @param contextRef : jscontextref
     * @return java jscontext
     */
    jobject getJSContext(JSGlobalContextRef contextRef);

    /**
     * get jsvalue manager
     * @param jsContext : java jscontext
     * @return jsvalue manager
     */
    ValueManager *getValueManager(jobject jsContext);

    void stroreClsObject(jobject obj);
    void removeClsObject();
};


#endif //NATIVE_JS_ANDROID_CONTEXTMANAGER_H
