from rest_framework import serializers
from classroom.models import *


class ProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = Profile
        fields = ('avatar', )


class OrganizationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Organization
        fields = ('id', 'name', 'admins', 'profile')
#        read_only_fields = ('admins',)


class DepartmentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Department
        fields = ('id', 'name', 'organization', 'profile')


class UserSerializer(serializers.ModelSerializer):
    avatar = serializers.SerializerMethodField()
    profile = serializers.SerializerMethodField()

    class Meta:
        model = User
        fields = ('id', 'first_name', 'last_name', 'username', 'password', 'email', 'profile', 'avatar')
        extra_kwargs = {'password': {'write_only': True}}

    def create(self, data):
        user = User(first_name=data['first_name'], last_name=data['last_name'], username=data['username'], email=['email'])
        user.set_password(data['password'])
        user.save()
        return user

    def get_profile(self, user):
        return UserProfile.objects.get(user__pk=user.id).profile.pk

    def get_avatar(self, user):
        profile = UserProfile.objects.get(user__pk=user.id)
        return profile.profile.avatar.url


class TeacherSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    user_id = serializers.PrimaryKeyRelatedField(source='user', queryset=User.objects.all())
    class Meta:
        model = Teacher
        fields = ('id', 'user', 'department', 'user_id', 'username')


class SubjectSerializer(serializers.ModelSerializer):
    class Meta:
        model = Subject
        fields = ('id', 'name', 'short_name', 'department')


class ClassSerializer(serializers.ModelSerializer):
    class Meta:
        model = Class
        fields = ('id', 'class_id', 'department', 'admins', 'profile')
#        read_only_fields = ('admins',)


class GroupSerializer(serializers.ModelSerializer):
    class Meta:
        model = Group
        fields = ('id', 'group_id', 'p_class')


class StudentSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    user_id = serializers.PrimaryKeyRelatedField(source='user', queryset=User.objects.all())
    class Meta:
        model = Student
        fields = ('id', 'user', 'group', 'user_id')
