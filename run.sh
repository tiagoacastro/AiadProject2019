#!/bin/bash


cars_type[0]="Car"
cars_type[1]="Rushed"
cars_type[2]="Ambulance"

nodes_init=(0 1 3 4 5)
nodes_end=(2 6 7 8 9 10 11)

function write_headers(){
	echo -e "false\nfalse\n1000\n8\n" > config.txt
}

function write_cars_config(){

	FLOOR=4
	number=0   #initialize
	while [ "$number" -le $FLOOR ]
	do
  		number=$RANDOM
	done
	let "number %= 14"
	echo $number
	number=$((number + 4))
	for i in $(seq 1 $number)
		do
			car_type=$RANDOM
			n_init=$RANDOM
			n_end=$RANDOM
			tries=$RANDOM
			let "car_type %= 3"
			let "n_init %= 5"
			let "n_end %= 7"
			let "tries %= 10"
			if (($car_type == 2))
			then 
				echo "${cars_type[$car_type]} ${nodes_init[$n_init]} ${nodes_end[$n_end]} 300 $tries" >> config.txt
			else
				randport=$(python -S -c "import random; print random.randrange(50,101)")
				echo "${cars_type[$car_type]} ${nodes_init[$n_init]} ${nodes_end[$n_end]} $randport $tries" >> config.txt
			fi
		done
}

write_headers
write_cars_config

/usr/lib/jvm/java-12-oracle/bin/java \
 -javaagent:/snap/intellij-idea-ultimate/191/lib/idea_rt.jar=42297:/snap/intellij-idea-ultimate/191/bin \
 -Dfile.encoding=UTF-8 \
 -classpath /home/joao/Desktop/4thY/AIAD/AiadProject2019/out/production/AiadProject2019:/home/joao/Downloads/JADE-all-4.5.0/jade/lib/jade.jar app.Main
