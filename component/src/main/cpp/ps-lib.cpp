#include <jni.h>
#include <string>

void qS(unsigned char array[], int size);

void _qS(unsigned char array[], int low, int high);

int findPivotIndex(unsigned char array[], int low, int high);


extern "C"
JNIEXPORT jstring JNICALL
Java_com_easymi_component_utils_EncApi_en(JNIEnv *env, jobject instance, jstring aesPassword ,jstring content_) {
    //获取AesCore
    jclass aesCoreClass = env->FindClass("com/easymi/component/utils/AesUtil");
    //获取aesEncrypt静态方法
    jmethodID aesEncrypt = env->GetStaticMethodID(aesCoreClass, "aesEncrypt",
                                                  "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");

//    //vipara & password 都为 RWA7XBFXQBA2S00E
//    //为了防止测评直接看到明码，做点小手段让他肉眼看不出来
//
//    //A到Z字母乱序
//    unsigned char a[27] = {'K', 'L', 'M', 'Y', 'Z', 'D', 'E', 'N', 'O', 'P', 'Q', 'R', 'U', 'H',
//                           'A', 'F', 'G', 'V', 'W', 'X', 'I', 'J', 'S', 'T', 'B', 'C'};
//    //0到9数组乱序
//    unsigned char b[11] = {'9', '1', '3', '4', '0', '5', '6', '7', '8', '2'};
//    int is[] = {17, 22, 0, 7, 23, 1, 5, 23, 16, 1, 0, 2, 18, 0, 0, 4};
//
//    qS(a, 26);
//    qS(b, 10);
//    char r[17] = {0};
//    for (int i = 0; i < 16; i++) {
//        r[i] = a[is[i]];
//    }
//    r[3] = b[7];
//    r[11] = b[2];
//    r[13] = b[0];
//    r[14] = b[0];
//
////    jstring vipara = env->NewStringUTF("RWA7XBFXQBA2S00E");
////    jstring password = env->NewStringUTF("RWA7XBFXQBA2S00E");
//
//    jstring vipara = env->NewStringUTF(r);
//    jstring password = env->NewStringUTF(r);

    jstring  str=(jstring)env->CallStaticObjectMethod(aesCoreClass,aesEncrypt,aesPassword,content_,aesPassword);

    if (str != NULL) {
        const char *newStr = env->GetStringUTFChars(str, 0);
        return env->NewStringUTF(newStr);
    } else {
        return NULL;
    }

}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_easymi_component_utils_EncApi_dec(JNIEnv *env, jobject instance, jstring aesPassword , jstring content_) {
    //获取AesCore
    jclass aesCoreClass = env->FindClass("com/easymi/component/utils/AesUtil");
    //获取aesEncrypt静态方法
    jmethodID aesDecrypt = env->GetStaticMethodID(aesCoreClass, "aesDecrypt",
                                                  "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
//
//    //vipara & password 都为 RWA7XBFXQBA2S00E
//    //为了防止测评直接看到明码，做点小手段让他肉眼看不出来
//
//    //A到Z字母乱序
//    unsigned char a[27] = {'K', 'L', 'M', 'Y', 'Z', 'D', 'E', 'N', 'O', 'P', 'Q', 'R', 'U', 'H',
//                           'A', 'F', 'G', 'V', 'W', 'X', 'I', 'J', 'S', 'T', 'B', 'C'};
//    //0到9数组乱序
//    unsigned char b[11] = {'9', '1', '3', '4', '0', '5', '6', '7', '8', '2'};
//    int is[] = {17, 22, 0, 7, 23, 1, 5, 23, 16, 1, 0, 2, 18, 0, 0, 4};
//
//    qS(a, 26);
//    qS(b, 10);
//
//    char r[17] = {0};
//    for (int i = 0; i < 16; i++) {
//        r[i] = a[is[i]];
//    }
//
//    r[3] = b[7];
//    r[11] = b[2];
//    r[13] = b[0];
//    r[14] = b[0];
//
////    jstring vipara = env->NewStringUTF("RWA7XBFXQBA2S00E");
////    jstring password = env->NewStringUTF("RWA7XBFXQBA2S00E");
//
//    jstring vipara = env->NewStringUTF(r);
//    jstring password = env->NewStringUTF(r);

    jstring str = (jstring) env->CallStaticObjectMethod(aesCoreClass, aesDecrypt, aesPassword, content_,
                                                        aesPassword);

    if (str != NULL) {
        const char *newStr = env->GetStringUTFChars(str, 0);
        return env->NewStringUTF(newStr);
    } else {
        return NULL;
    }
}


void qS(unsigned char array[], int size) {
    _qS(array, 0, size - 1);
}

void _qS(unsigned char array[], int low, int high) {
    int pivotIndex = 0;
    if (low < high) {
        pivotIndex = findPivotIndex(array, low, high);
        _qS(array, low, pivotIndex - 1);
        _qS(array, pivotIndex + 1, high);
    }
}

int findPivotIndex(unsigned char array[], int low, int high) {
    unsigned char pivot = array[low];
    while (low < high) {
        while (low < high && array[high] >= pivot) { --high; }
        array[low] = array[high];
        while (low < high && array[low] <= pivot) { ++low; }
        array[high] = array[low];
    }
    array[low] = pivot;
    return low;
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_easymi_component_utils_EncApi_getPubKey(JNIEnv *env, jobject instance) {
    std::string value = "30820122300d06092a864886f70d01010105000382010f003082010a0282010100c7a9678f07f018dd8ec1dc6b24be520bce1662ad26651397469d9c986a91032c6da2c87a90cca28581cb4f8a8f1cb35fb4b1e4c30ba8ecb24ae03a466cf0efd3453d27c2b0600e55308b09b16c0aea9d693e028346a8f5fe684dd1544fde9c0acb3052803cfd63f9759f48c3fc8761ed474c18c0dd4b3f5425fd82e13dcb93b73637a2551322275498fac0ff6d023fead9aa04c5b89d130e5684f19e4bc0e2a7461d4b4f469281564400c1fda78211a16f49f69a0d8f17f10c86105ab27910be0a59a805643535aee7175b5a2083d0c4a53ab97ec97a088f036252b30f5337e0c18355874d61fe8d4a255eb5665c17be62cbba7607d6a40a7372243567b7b6b70203010001";
    return env->NewStringUTF(value.c_str());
}

