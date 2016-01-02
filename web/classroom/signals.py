from django.contrib.auth.models import User, UserManager
from django.db.models.signals import post_save

from classroom.models import *

def create_new_profile(sender, instance, created, **kwargs):
    print("got")
    if created:
        print("created")
        profile = UserProfile(user=instance)
        profile.save()

post_save.connect(create_new_profile, sender=User)
