from django.contrib.auth.models import User, UserManager
from django.db.models.signals import post_save

from posts.models import *
from posts.utils import *
from main.models import *
from main.notify import *
from classroom.utils import *


def new_post(sender, instance, created, **kwargs):
    post = instance
    if created:
        title = ""
        if post.title and post.title != "":
            title = post.title

        users = get_post_users(post)
        upks = list(set([u.pk for u in users]))

        tokens = GcmRegistration.objects.filter(user__pk__in=upks).values_list('token', flat=True)
        notify(tokens, getUserName(post.posted_by), title)

post_save.connect(new_post, sender=Post)
