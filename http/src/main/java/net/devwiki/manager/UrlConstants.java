package net.devwiki.manager;

/**
 * URL常量
 * @author DevWiki
 */
public interface UrlConstants {

    interface Header {
        String SPLIT_COLON = ":";
        String HOST = "HostName";
        String HOST_CLOUD = HOST + SPLIT_COLON + HostName.CLOUD;
        String HOST_PRIVATE = HOST + SPLIT_COLON + HostName.PRIVATE;
        String HOST_DEV = HOST + SPLIT_COLON + HostName.DEV;
        String REST_VERSION = "RestVersion";
        String REST_VERSION_V1 = REST_VERSION + SPLIT_COLON + RestVersionCode.V1;
        String REST_VERSION_V2 = REST_VERSION + SPLIT_COLON + RestVersionCode.V2;
        String REST_VERSION_PRIVATE = REST_VERSION + SPLIT_COLON + RestVersionCode.PRIVATE;
        String REST_VERSION_EMPTY = REST_VERSION + SPLIT_COLON + RestVersionCode.EMPTY;
    }

    interface HostName {
        String CLOUD = "CLOUD";
        String PRIVATE = "PRIVATE";
        String DEV = "DEV";
    }

    interface HostValue {
        String CLOUD = "https://www.baidu.com";
        String PRIVATE = "https://private.bidu.com";
        String DEV = "https://dev.baidu.com";
    }

    enum Host {
        CLOUD(HostName.CLOUD, HostValue.CLOUD),
        PRIVATE(HostName.PRIVATE, HostValue.PRIVATE),
        DEV(HostName.DEV, HostValue.DEV);

        private String name;
        private String value;
        Host(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * path 前缀一览
     */
    enum Rest {
        EMPTY(RestVersionCode.EMPTY, RestVersionValue.EMPTY),
        V1(RestVersionCode.V1, RestVersionValue.V1),
        V2(RestVersionCode.V2, RestVersionValue.V2),
        PRIVATE(RestVersionCode.PRIVATE, RestVersionValue.PRIVATE);

        private String version;
        private String value;
        Rest(String version, String value) {
            this.version = version;
            this.value = value;
        }

        public String getVersion() {
            return version;
        }

        public String getValue() {
            return value;
        }
    }

    interface RestVersionCode {
        String EMPTY = "EMPTY";
        String V1 = "V1";
        String V2 = "V2";
        String PRIVATE = "PRIVATE";
    }

    /**
     * path 前缀值
     */
    interface RestVersionValue {
        String EMPTY = "";
        String V1 = "rest/v1";
        String V2 = "rest/v2";
        String PRIVATE = "rest/private";
    }
}
