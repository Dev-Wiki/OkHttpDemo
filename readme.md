## 动态改变Retrofit的 base url和 path

### base url 类型

默认base url: `https://cloud.devwiki.net`

测试版 url : `https://dev.devwiki.net`

私有云版本url: `https://private.devwiki.net`

### rest 版本

- `/rest/v1/`
- `/rest/v2/`
- `/rest/v3/`

### 需求

1. 大部分接口使用 cloud host, 部分接口使用 private host
2. 大部分接口使用 rest/v3 版本, 部分接口使用 v2, v1版本.
3. 每个host 都有可能存在 rest v1, v2, v3的接口