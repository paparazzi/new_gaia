SRC_PATH = ./src
CLASS_PATH = ./classes

IVY_PATH = ./jars/ivy-java.jar
JDOM_PATH = ./jars/jdom-2.0.1.jar
SWINGSTATES_PATH = ./jars/SwingStates.jar

JC = javac
J = java

RM = rm -f
CP = -cp
DIR = -d

default :
	$(JC) $(CP) $(JDOM_PATH):$(IVY_PATH):$(SWINGSTATES_PATH) $(DIR) $(CLASS_PATH) $(SRC_PATH)/*.java

clean :
	$(RM) $(CLASS_PATH)/*.class

run :
	$(J) $(CP) $(SWINGSTATES_PATH):$(JDOM_PATH):$(IVY_PATH):$(CLASS_PATH) MainWindow
