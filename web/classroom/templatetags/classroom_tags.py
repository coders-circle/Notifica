from django import template
from classroom.users import *
from classroom.links import *

import json

register = template.Library()

@register.filter
def user_type(user):
    return getUserType(user)


@register.filter
def json_decode(string):
    return json.loads(string)


@register.filter
def linked_object(string):
    return getLinkedObject(string)
