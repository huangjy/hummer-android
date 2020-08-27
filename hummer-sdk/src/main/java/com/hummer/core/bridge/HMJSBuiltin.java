package com.hummer.core.bridge;

public class HMJSBuiltin {
    public static String _HMJSBuiltinFunc = new StringBuffer(
            "const __GLOBAL__ = this;\n" +
            "class HMJSObject extends Object {\n" +
            "    constructor(...args){\n" +
            "        super(...args);\n" +
            "        this.private = this.getPrivate(...args);\n" +
            "        this.registerMethods();\n" +
            "        this.registerVariables();\n" +
            "        this.initialize(...args);\n" +
            "    }\n" +
            "    registerMethods(){\n" +
            "        for(let method of this.private.methods){\n" +
            "            this[method] = function(...args){\n" +
            "                return this.private[method](...args);\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "    registerVariables(){\n" +
            "       for(let variable of this.private.variables){\n" +
            "           var self = this;\n" +
            "           Object.defineProperty(self, variable, {\n" +
            "               get : function(){ return self.private[variable]; },\n" +
            "               set : function(newValue){ self.private[variable] = newValue; },\n" +
            "               enumerable : true,\n" +
            "               configurable : true,\n" +
            "           })\n" +
            "       }\n" +
            "    }\n" +
            "    getPrivate(...args){ return null; }\n" +
            "    initialize(){ }\n" +
            "    finalize(){ }\n" +
            "}\n" +
            "class HMJSUtility extends Object {\n" +
            "    static createClass(className){\n" +
        	"		return ({\n" + 
            "			[className] : class extends HMJSObject {\n" + 
            "    			getPrivate(...args){\n" + 
            "        			var nativeClass = 'NATIVE_' + className;\n" +
            "        			return __GLOBAL__[nativeClass](...args);\n" +
            "				}\n" +
            "			}\n" + 
            "    	})[className];\n" +
            "    }\n" +
            "    static registerStatics(className, methods){\n" +
            "        for(let method of methods){\n" +
            "            __GLOBAL__[className][method] = function(...args){\n" +
            "                return Hummer.callFunc(className, method, args);\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "    static initGlobalEnv(classList){\n" +
            "        for(let className in classList){\n" +
            "           let privateClass = this.createClass(className);\n" +
            "            Object.defineProperty(__GLOBAL__, className, {value: privateClass});\n" +
            "            this.registerStatics(className, classList[className]);\n" +
            "        }\n" +
            "    }\n" +
            "}"
    ).toString();

    public static String HMGlabalFuntion =new StringBuffer(
            "function HMPoint(x,y) {\n" +
            "    return {'type':'CGPoint','x':x,'y':y};\n" +
            "}\n" +
            "function HMRect(x,y,width,height) {\n" +
            "    return {'type':'CGRect','x':x,'y':y, 'width':width, 'height':height };\n" +
            "}"
    ).toString();
}
