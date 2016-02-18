from rest_framework import serializers
from main.models import *


class RequestSerializer(serializers.ModelSerializer):
    class Meta:
        model = Request
        fields = ('id', 'sender', 'sender_type', 'status', 'request_type', 'to')


class GcmRegistrationSerializer(serializers.ModelSerializer):
    class Meta:
        model = GcmRegistration
        fields = ('id', 'user', 'device_id', 'token')
        read_only_fields = ('user',)
