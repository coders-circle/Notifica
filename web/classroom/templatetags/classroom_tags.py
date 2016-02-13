from django import template
from classroom.utils import *

import json
from datetime import datetime

register = template.Library()


@register.filter
def json_decode(string):
    return json.loads(string)


@register.filter
def to_iso(date):
    return date.isoformat(' ')
