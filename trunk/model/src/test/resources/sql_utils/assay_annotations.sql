select ass.*, at.VALUE, a.TEXT 
from assay ass
left join ASSAY_ANNOTATION a2a on ass.ID = a2a.ASSAY_ID
left join ANNOTATION a on a2a.ANNOTATIONS_ID = a.ID
left join ANNOTATION_TYPE at ON a.TYPE_ID = at.ID
