from main.models import *
import requests
import json


def notify(tokens, title, text):
    url = "https://gcm-http.googleapis.com/gcm/send"
    tokens = list(tokens)

    message = {
        "data" : {
            "title": title,
            "body": text
        },
        "registration_ids":tokens
    }

    key = "key=AIzaSyAOLOf551nMPy2XahWYVKRcSTa_9Uj8vQc"
    headers = {'Content-type':'application/json', 'Authorization':key}
    print(message)
    r = requests.post(url, data=json.dumps(message), headers=headers)

    response = json.loads(r.text)
    print(r.text)

    # TODO: https://developers.google.com/cloud-messaging/http#response
    # Handle response
