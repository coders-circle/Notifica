from django.db import models
from django.contrib.auth.models import User, UserManager


# Requests

REQUEST_STATUS = (
    (0, 'Unaccepted'),      # when sender requests
    (1, 'Accepted'),        # when receiver accepts
    (2, 'Rejected'),        # when receiver rejects
    (3, 'Acknowledged'),    # when sender knows acceptance/rejection
)

REQUEST_TYPES = (
    (0, 'JOIN_CLASS'),
    (1, 'JOIN_DEPARTMENT'),
)

SENDER_TYPES = (
    (0, 'USER'),
    (1, 'CLASS'),
)


class Request(models.Model):
    sender = models.IntegerField()
    sender_type = models.IntegerField(choices=SENDER_TYPES)
    status = models.IntegerField(default=0, choices=REQUEST_STATUS)
    request_type = models.IntegerField(choices=REQUEST_TYPES)
    to = models.IntegerField()

    def __str__(self):
        return REQUEST_TYPES[self.request_type][1] + " from " + \
            str(self.sender) + " to " + str(self.to)


# GCM Registrations

class GcmRegistration(models.Model):
    user = models.ForeignKey(User)
    device_id = models.TextField(default="")
    token = models.TextField()

    def __str__(self):
        return self.user.username + " : " + self.device_id
