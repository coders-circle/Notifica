from django.db.models import Q

from classroom.models import *
from posts.models import *


def get_post_users(post):
    profile, ptype = getProfileObject(post.profile)

    if ptype == "Class":
        users1 = User.objects.filter(student__group__p_class__pk=profile.pk)
        users2 = User.objects.filter(
            teacher__period__routine__p_class__pk=profile.pk)
        return users1 | users2

    elif ptype == "Department":
        users1 = User.objects.filter(
            student__group__p_class__department__pk=profile.pk)
        users2 = User.objects.filter(
            teacher__period__routine__p_class__department__pk=profile.pk)
        users3 = User.objects.filter(teacher__department__pk=profile.pk)
        return users1 | users2 | users3

    elif ptype == "User":
        return User.objects.filter(pk=profile.pk)

    else:
        return User.objects.none()
