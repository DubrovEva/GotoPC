new_applications = open("/home/eva/Программирование/new_application.txt")
participants = open("/home/eva/Программирование/participants.txt")
matches = open("/home/eva/Программирование/matches.txt")

man_and_money = {}
part = participants.readline().split()
while part != []:
    man_and_money[part[0]] = int(part[1])
    part = participants.readline().split()

old_applications = {}

new_appl = new_applications.readline().split()
while new_appl != []:
    if man_and_money[new_appl[1]] - int(new_appl[4]) >= 0:
        man_and_money[new_appl[0]] += int(new_appl[4])
        man_and_money[new_appl[1]] -= int(new_appl[4])
        old_applications[new_appl[3]] = [new_appl[0], new_appl[1], new_appl[2], new_appl[4]]
    new_appl = new_applications.readline().split()

matc = matches.readline().split()
while matc != []:
    if matc[0] in old_applications:
        if old_applications[matc[0]][2] == matc[1]:
            man_and_money[old_applications[matc[0]][0]] -= 2*int(old_applications[matc[0]][3])
            man_and_money[old_applications[matc[0]][1]] += 2*int(old_applications[matc[0]][3])
    matc = matches.readline().split()

new_applications = open("/home/eva/Программирование/new_application.txt", "w")
participants = open("/home/eva/Программирование/participants.txt", "w")
for i in man_and_money:
    participants.write(" ".join([str(i), str(man_and_money[i])])+"\n") 
matches = open("/home/eva/Программирование/matches.txt", "w")
