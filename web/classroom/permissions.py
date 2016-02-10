from rest_framework import permissions
from classroom.models import *


class IsAdminOrReadOnly(permissions.BasePermission):

    def has_object_permission(self, request, view, obj):
        if request.method in permissions.SAFE_METHODS or request.user.is_superuser:
            return True

        # users can modify themselves
        if obj == request.user:
            return True

        # department and class are maintained by their admins
        if hasattr(obj, 'admins'):
            return request.user in obj.admins.all()

        # group and routine belongs to a class
        if hasattr(obj, 'p_class'):
            return request.user in obj.p_class.admins.all()

        # period belongs to a routine which belongs to a class
        if hasattr(obj, 'routine'):
            return request.user in obj.routine.p_class.admins.all()

        # teacher and subject belong to a department
        if hasattr(obj, 'department'):
            return request.user in obj.department.organization.admins.all()

        # student belongs to a group
        if hasattr(obj, 'group'):
            return request.user in obj.group.p_class.admins.all()

        return False


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
