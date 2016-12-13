package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        url '/v1/notifications/search/findByUserId?userId=foo@example.com'
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 200
        body('''{
  "_embedded": {
    "notifications": [
      {
        "notificationType": "INFO",
        "notificationMessage": "info2!",
        "userId": "foo@example.com",
      },
      {
        "notificationType": "INFO",
        "notificationMessage": "info1!",
        "userId": "foo@example.com",
      }
    ]
  },
  "page": {
    "size": 20,
    "totalElements": 2,
    "totalPages": 1,
    "number": 0
  }
}''')
        headers {
            contentType("application/hal\\+json;charset=UTF-8")
        }
    }
}