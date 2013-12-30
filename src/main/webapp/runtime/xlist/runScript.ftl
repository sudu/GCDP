{
"success":${hasError?string("false","true")},
 "errorMessage":"${msg!""}",
 "message":"${dataPool.msg!""}",
 "refresh":${dataPool.refresh?string("true","false")},
 "data":${dataPool.data!"''"}
}