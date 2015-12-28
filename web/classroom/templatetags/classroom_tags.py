from django import template
from classroom.utils import *

import json

register = template.Library()


@register.filter
def json_decode(string):
    return json.loads(string)
