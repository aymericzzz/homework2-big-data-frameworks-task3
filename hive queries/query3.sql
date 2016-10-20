SELECT sum(if(g='m',1,0))/count(*) AS m_prop, sum(if(g='f',1,0))/count(*) AS f_prop 
FROM (
SELECT prenom, g
FROM prenoms LATERAL VIEW explode(prenoms.gender) genders AS g
) A;