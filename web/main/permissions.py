from rest_framework import permissions
from main.models import *
from classroom.models import *


def FilterRequest(obj, user):
    if user.is_superuser:
        return True

    if obj.sender_type == 0 and obj.sender == user.pk:
        return True

    if obj.sender_type == 1 and \
        user in Class.objects.get(pk=obj.sender).admins.all():
        return True

    if obj.request_type == 0 and \
        user in Class.objects.get(pk=obj.to).admins.all():
        return True

    if obj.request_type == 1 and \
        user in Department.objects.get(pk=obj.to).admins.all():
        return True

    return False


class IsRequestOwner(permissions.BasePermission):

    def has_object_permission(self, request, view, obj):
        return FilterRequest(obj, request.user)
