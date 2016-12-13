package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        url '/v1/notifications/search/findByUserId?userId=maki@example.com'
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
        "notificationType": "WARN",
        "notificationMessage": "warn1!",
        "userId": "maki@example.com",
      },
      {
        "notificationType": "ERROR",
        "notificationMessage": "error5!",
        "userId": "maki@example.com",
      },
      {
        "notificationType": "ERROR",
        "notificationMessage": "error4!",
        "userId": "maki@example.com",
      },
      {
        "notificationType": "ERROR",
        "notificationMessage": "error3!",
        "userId": "maki@example.com",
      },
      {
        "notificationType": "ERROR",
        "notificationMessage": "error2!",
        "userId": "maki@example.com",
      },
      {
        "notificationType": "ERROR",
        "notificationMessage": "error1!",
        "userId": "maki@example.com",
      }
    ]
  },
  "page": {
    "size": 20,
    "totalElements": 6,
    "totalPages": 1,
    "number": 0
  }
}''')
        headers {
            contentType("application/hal\\+json;charset=UTF-8")
        }
    }
}