from django.db import models
from django.contrib.auth.models import User, UserManager

import random


def getProfileObject(profile):
    try:
        c = Class.objects.get(profile=profile)
        return c, "Class"
    except:
        try:
            d = Department.objects.get(profile=profile)
            return d, "Department"
        except:
            try:
                u = UserProfile.objects.get(profile=profile)
                return u, "User"
            except:
                return None, "None"


class Profile(models.Model):
    avatar = models.ImageField(upload_to='avatars/', default='avatars/ninja.png', blank=True)

    def __str__(self):
        obj, kind = getProfileObject(self)
        if kind == "Organization" or kind =="Department":
            return obj.name
        elif kind == "Class":
            return obj.class_id
        elif kind == "User":
            return obj.user.username
        else:
            return "Unknown Profile"


class Organization(models.Model):
    name = models.CharField(max_length=50)

    def __str__(self):
        return self.name


class Department(models.Model):
    name = models.CharField(max_length=50)
    admins = models.ManyToManyField(User)
    organization = models.ForeignKey(Organization)
    profile = models.OneToOneField(Profile)

    def __str__(self):
        return self.name + ", " + str(self.organization)


class Teacher(models.Model):
    user = models.ForeignKey(User, null=True, blank=True)
    username = models.CharField(max_length=50, null=True, blank=True)
    department = models.ForeignKey(Department, null=True, blank=True)

    def __str__(self):
        if self.user:
            return self.user.first_name + " " + self.user.last_name
        elif self.username:
            return self.username
        else:
            return "unnamed"


def get_random_color():
    r = lambda: random.randint(0,255)
    return('#%02X%02X%02X'%(r(), r(), r()))


class Subject(models.Model):
    name = models.CharField(max_length=50)
    short_name = models.CharField(max_length=5)
    department = models.ForeignKey(Department, null=True, blank=True)
    color = models.CharField(max_length=10, default=get_random_color)

    def __str__(self):
        return self.name

    def save(self, *args, **kwargs):
        if not self.short_name or self.short_name == "":
            self.short_name = ''.join(n[0].upper() for n in self.name.split())
        super(Subject, self).save(*args, **kwargs)


class Class(models.Model):
    class_id = models.CharField(max_length=30)
    description = models.CharField(max_length=300, blank=True, default="")
    department = models.ForeignKey(Department, null=True, blank=True)
    admins = models.ManyToManyField(User)
    profile = models.OneToOneField(Profile)

    class Meta:
        verbose_name_plural = "classes"

    def __str__(self):
        return self.class_id


class Group(models.Model):
    group_id = models.CharField(max_length=10)
    p_class = models.ForeignKey(Class, verbose_name="class")

    def __str__(self):
        return self.p_class.class_id + "(" + self.group_id + ")"


class Student(models.Model):
    user = models.ForeignKey(User)
    group = models.ForeignKey(Group)

    class Meta:
        verbose_name = "student"

    def __str__(self):
        return self.user.username


class Elective(models.Model):
    group = models.CharField(max_length=30, default="A")
    p_class = models.ForeignKey(Class, verbose_name="class")
    subject = models.ForeignKey(Subject)
    students = models.ManyToManyField(Student, blank=True)

    def __str__(self):
        return str(self.subject) + " (" + self.group + ") - " + str(self.p_class)


class UserProfile(models.Model):
    user = models.OneToOneField(User, primary_key=True)
    profile = models.OneToOneField(Profile, blank=True)

    def save(self, *args, **kwargs):
        exist = True
        try:
            p = self.profile
            if not p:
                exist = False
        except:
            exist = False

        if not exist:
            p = Profile()
            p.save()
            self.profile = p
        super(UserProfile, self).save(*args, **kwargs)
        
    def __str__(self):
        return self.user.username


from classroom import signals
