from django import template

register = template.Library()

@register.filter
def item(value, arg):
    try:
        return value[arg]
    except:
        return None

 
