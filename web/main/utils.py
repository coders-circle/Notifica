from django.db.models import Q
from main.models import *
from classroom.models import *
from classroom.utils import *


def create_request_notification(request, object, raw=False):

    notification = {
        "type": "request",
        "request": request.pk if raw else request
    }

    if request.request_type == 0:
        notification["class"] = object.pk if raw else object

    if request.sender_type == 0:
        user = User.objects.get(pk=request.sender)
        notification["sender"] = user.pk if raw else user
        notification["sender_name"] = getUserName(user)
    return notification


def get_notifications(user, raw=False):
    # Get all requests made to class or department user is admin of
    my_requests = []
    class_requests = Request.objects.filter(status=0, request_type=0)
    for cr in class_requests:
        try:
            p_class = Class.objects.get(pk=cr.to, admins__pk=user.pk)
            my_requests.append(create_request_notification(cr, p_class, raw))
        except:
            pass

    # TODO: Department join request
    #
    #

    return my_requests
