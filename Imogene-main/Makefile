JAVAC = javac
JAVA = java

SRC_DIR = src
BIN_DIR = bin
MAIN_CLASS = com.application.Application
BACKEND_CLASS = com.backend.BackendApplication

# Find all .java files recursively
SOURCES := $(shell find $(SRC_DIR) -name "*.java")

# Default target
all:
	@mkdir -p $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) $(SOURCES)

run: all
	$(JAVA) -cp $(BIN_DIR) $(MAIN_CLASS)

backend: all
	$(JAVA) -cp $(BIN_DIR) $(BACKEND_CLASS)

clean:
	rm -rf $(BIN_DIR)

.PHONY: all run clean
