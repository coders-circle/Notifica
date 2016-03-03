from django.db.models import Q

from rest_framework import viewsets, permissions
from rest_framework.views import APIView
from rest_framework.response import Response

from main.models import *
from main.utils import *
from main.permissions import *
from main.serializers import *


class RequestViewSet(viewsets.ModelViewSet):
    serializer_class = RequestSerializer
    permission_classes = (permissions.IsAuthenticated, IsRequestOwner)

    def perform_create(self, serializer):
        sender_type = serializer.validated_data["sender_type"]
        sender = serializer.validated_data["sender"]

        if sender_type == 1 and self.request.user not in \
                Class.objects.get(pk=sender).admins.all():
            raise serializers.ValidationError("You have not permission " +
                                              "to send request on behalf of " +
                                              "this class")
        serializer.save()

    def get_queryset(self):
        queryset = Request.objects.all()
        filtered = []

        for p in queryset:
            if filter_request(p, self.request.user):
                filtered.append(p.pk)

        return Request.objects.filter(pk__in=filtered)


class GcmRegistrationViewSet(viewsets.ModelViewSet):
    serializer_class = GcmRegistrationSerializer
    perimission_classes = (permissions.IsAuthenticated,)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

    def get_queryset(self):
        queryset = GcmRegistration.objects.filter(
            user__pk=self.request.user.pk
        )

        device_id = self.request.GET.get("device")
        if device_id:
            queryset = queryset.filter(device_id=device_id)

        return queryset


class NotificationView(APIView):
    def get(self, request, format=None):
        return Response(get_notifications(request.user, True))
