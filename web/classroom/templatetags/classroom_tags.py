from django import template
from classroom.users import *

register = template.Library()

@register.filter
def user_type(user):
    return getUserType(user)
