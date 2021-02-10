SRC_PATH = ./src
CLASS_PATH = ./classes

IVY_PATH = ./jars/ivy-java.jar
JDOM_PATH = ./jars/jdom-2.0.6.jar
SWINGSTATES_PATH = ./jars/SwingStates.jar

JC = javac
J = java

RM = rm -f
CP = -cp
DIR = -d

all :
	mkdir -p $(CLASS_PATH)
	$(JC) $(CP) $(JDOM_PATH):$(IVY_PATH):$(SWINGSTATES_PATH) $(DIR) $(CLASS_PATH) $(SRC_PATH)/*.java

clean :
	$(RM) $(CLASS_PATH)/*.class

run :
	$(J) $(CP) $(SWINGSTATES_PATH):$(JDOM_PATH):$(IVY_PATH):$(CLASS_PATH) MainWindow

download_jars:
	mkdir -p jars
	cd jars && \
	wget --no-check-certificate https://www.eei.cena.fr/products/ivy/download/packages/ivy-java-1.2.6.jar && \
	ln -s ivy-java-1.2.6.jar ivy-java.jar && \
	wget http://jdom.org/dist/binary/jdom-2.0.6.zip && \
	unzip jdom-2.0.6.zip && \
	wget http://downloads.sourceforge.net/project/swingstates/swingstates/swingstates_0.3/swingstates_0.3_14_02_18.zip && \
	unzip swingstates_0.3_14_02_18.zip swingstates_0.3_14_02_18/SwingStates.jar && \
	ln -s swingstates_0.3_14_02_18/SwingStates.jar SwingStates.jar
