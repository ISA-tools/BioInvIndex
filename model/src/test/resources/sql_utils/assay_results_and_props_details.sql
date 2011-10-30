select distinct pv.VALUE, p.VALUE as property, p.ROLE, p.OBJ_TYPE, s.ACC as study_acc
from ASSAYRESULT ar
left join STUDY s on ar.STUDY_ID = s.ID
left join ASSAYRESULT2PROPERTYVALUE a2pv on ar.ID = a2pv.AR_ID
left join PROPERTY_VALUE pv on a2pv.PV_ID = pv.ID
left join PROPERTY p on pv.PROPERTY_ID = p.ID

