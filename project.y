%{
	#include <stdio.h>
	#include <iostream>
	#include <string>
	#include <vector>
	#include <map>
	using namespace std;
	#include "y.tab.h"
	extern FILE 		*yyin;
	extern int			yylex();
	void				yyerror(string s);
	bool				search(string str, string ctrl);
	int					num_of_tabs=0;
	int					linenum=1;
	int					maximum_number_of_tabs_required = 0;
	bool 				precision = false;
	vector<bool> 		open_if_vector;
	string              int_vars = "";
	string				float_vars = "";
	string				string_vars = "";
	map<string, int>	valids;
	bool				is_plus;
	string				string_sign;
	int					status[2];
	string				cpp = "void main()\n{\n";
	string				tmp = "";
	string				assignment = "";
	string				elm;
	char				*variable;
%}

%union
{
	char	*str;
}

%token <str> VAR STRING PLUS MINUS POINT SLASH INT FLOAT EQUAL IF ELIF ELSE COMPARISON NL COLON TAB

%%

	statement:
		states
		|
		nls states
		;

	nl:
		NL{linenum++;};

	nls:
		nl
		|
		nl nls
		;

	states:
		|
		state
		|
		state nls states
		;

	tab:
		TAB{num_of_tabs++;};

	comparison:
		COMPARISON
		{
			string element($1);
			free($1);
			tmp += " " + element;
		}
		;

	if:
		IF
		{
			for (int i = maximum_number_of_tabs_required; i >= num_of_tabs + 1; i--) {
				for (int j = 0; j < i; j++)
					tmp += "\t";
				tmp += "}\n";
			}
			for (int i = 0; i <= num_of_tabs; i++)
				tmp += "\t";
			tmp += "if(";
		}
		;

	elif:
		ELIF
		{
			for (int i = maximum_number_of_tabs_required; i >= num_of_tabs + 1; i--) {
				for (int j = 0; j < i; j++)
					tmp += "\t";
				tmp += "}\n";
			}
			for (int i = 0; i <= num_of_tabs; i++)
				tmp += "\t";
			tmp += "else if (";
		}
		;

	else:
		ELSE
		{
			for (int i = maximum_number_of_tabs_required; i >= num_of_tabs + 1; i--) {
				for (int j = 0; j < i; j++)
					tmp += "\t";
				tmp += "}\n";
			}
			for (int i = 0; i <= num_of_tabs; i++)
				tmp += "\t";
			tmp += "else\n";
		}
		;

	var:
		VAR
		{
			variable = $1;
			for (int i = maximum_number_of_tabs_required; i >= num_of_tabs + 1; i--) {
				for (int j = 0; j < i; j++)
					tmp += "\t";
				tmp += "}\n";
			}
			for (int i = 0; i <= num_of_tabs; i++)
				tmp += "\t";
		}
		;
	state:
		tab state
		|
		if sign{tmp += string_sign;} operand {status[0] = status[1]; tmp += elm; if (string_sign != "" && status[0] == 3) {
					cout << "type inconsistency in line " << linenum << endl;
					exit(1);
				}} comparison sign{tmp += string_sign;} operand {tmp += elm + " )\n";} COLON
		{
			for (int i = 0; i <= num_of_tabs; i++)
				tmp += "\t";
			tmp += "{\n";
			if ((precision && num_of_tabs != maximum_number_of_tabs_required) || num_of_tabs > maximum_number_of_tabs_required) {
				cout << "there is a tab inconsistency in line " << linenum << endl;
				exit(1);
			}
			if (num_of_tabs >= open_if_vector.size())
				open_if_vector.push_back(true);
			else
				open_if_vector[num_of_tabs] = true;
			if (num_of_tabs < maximum_number_of_tabs_required) {
				while (open_if_vector.size() != num_of_tabs + 1)
					open_if_vector.pop_back();
				maximum_number_of_tabs_required = num_of_tabs;
			}
			maximum_number_of_tabs_required++;
			precision = true;
			num_of_tabs = 0;
			if ((status[0] == 3 || status[1] == 3) && ((status[0] != 3 || status[1] != 3) || (string_sign != "" && status[1] == 3)))
			{
				cout << "type inconsistency in line " << linenum << endl;
				exit(1);
			}
			if (status[0] == 1 && status[1] == 2)
				status[0] = 2;
		}
		|
		elif sign{tmp += string_sign;} operand {status[0] = status[1]; tmp += elm; if (string_sign != "" && status[0] == 3) {
					cout << "type inconsistency in line " << linenum << endl;
					exit(1);
				}} comparison sign{tmp += string_sign;} operand {tmp += elm + " )\n";} COLON
		{
			for (int i = 0; i <= num_of_tabs; i++)
				tmp += "\t";
			tmp += "{\n";
			if (precision || num_of_tabs > maximum_number_of_tabs_required) {
				cout << "there is a tab inconsistency in line " << linenum << endl;
				exit(1);
			}
			if (num_of_tabs < maximum_number_of_tabs_required) {
				while (open_if_vector.size() != num_of_tabs + 1)
					open_if_vector.pop_back();
				maximum_number_of_tabs_required = num_of_tabs;
			}
			if (num_of_tabs >= open_if_vector.size() ||  !open_if_vector[num_of_tabs]) {
				cout << "if/else consistency in line " << linenum << endl;
				exit(1);
			}
			maximum_number_of_tabs_required++;
			precision = true;
			num_of_tabs = 0;
			if ((status[0] == 3 || status[1] == 3) && ((status[0] != 3 || status[1] != 3) || (string_sign != "" && status[1] == 3)))
			{
				cout << "type inconsistency in line " << linenum << endl;
				exit(1);
			}
			if (status[0] == 1 && status[1] == 2)
				status[0] = 2;
		}
		|
		else
		{
			if (precision || num_of_tabs > maximum_number_of_tabs_required) {
				cout << "there is a tab inconsistency in line " << linenum << endl;
				exit(1);
			}
			if (num_of_tabs < maximum_number_of_tabs_required) {
				while (open_if_vector.size() != num_of_tabs + 1)
					open_if_vector.pop_back();
				maximum_number_of_tabs_required = num_of_tabs;
			}
			for (int i = 0; i <= num_of_tabs; i++)
				tmp += "\t";
			tmp += "{\n";
			if (num_of_tabs >= open_if_vector.size() ||  !open_if_vector[num_of_tabs]) {
				cout << "if/else consistency in line " << linenum << endl;
				exit(1);
			}
			open_if_vector[num_of_tabs] = false;
			maximum_number_of_tabs_required++;
			precision = true;
			num_of_tabs = 0;
		}
		|
		var EQUAL sign{assignment = string_sign;} assignmentop
		{
			string	element(variable);
			free(variable);
			if ((precision && num_of_tabs != maximum_number_of_tabs_required) || num_of_tabs > maximum_number_of_tabs_required) {
				cout << "there is a tab inconsistency in line " << linenum << endl;
				exit(1);
			}
			if (num_of_tabs < maximum_number_of_tabs_required) {
				while (open_if_vector.size() != num_of_tabs)
					open_if_vector.pop_back();
				maximum_number_of_tabs_required = num_of_tabs;
			}
			precision = false;
			num_of_tabs = 0;
			if (status[0] == 1) {
				if (!search(int_vars, element + "_int")) {
					if (int_vars.length())
						int_vars += ",";
					int_vars += element + "_int";
				}
				tmp += element + "_int =";
				valids[element] = 1;
			}
			else if (status[0] == 2) {
				if (!search(float_vars, element + "_flt")) {
					if (float_vars.length())
						float_vars = "," + float_vars;
					float_vars = element + "_flt" + float_vars;
				}
				tmp += element + "_flt =";
				valids[element] = 2;
			}
			else if (status[0] == 3) {
				if (string_sign != "") {
					cout << "type inconsistency in line " << linenum << endl;
					exit(1);
				}
				if (!search(string_vars, element + "_str")) {
					if (string_vars.length())
						string_vars = "," + string_vars;
					string_vars = element + "_str" + string_vars;
				}
				tmp += element + "_str =";
				valids[element] = 3;
			}
			tmp += assignment + ";\n";
		}
		;

	assignmentop:
		operand {status[0] = status[1];}
		|
		assignmentop operator operand
		{
			if ((status[0] == 3 || status[1] == 3) && (status[0] != 3 || status[1] != 3 || !is_plus)) {
				cout << "type inconsistency in line " << linenum << endl;
				exit(1);
			}
			if (status[0] == 1 && status[1] == 2)
					status[0] = 2;
		}
		;

	operand:
		VAR {
			status[1] = valids[$1];
			if (!status[1]) {
				cout << "name '" << $1 << "' is not defined in line " << linenum << endl;
				free($1);
				exit(1);
			}
			else if (status[1] == 1) {
				elm = string($1) + "_int";
				if (assignment != string_sign || string_sign == "")
					elm = " " + elm;
			}
			else if (status[1] == 2) {
				elm = string($1) + "_flt";
				if (assignment != string_sign || string_sign == "")
					elm = " " + elm;
			}
			else if (status[1] == 3) {
				elm = string($1) + "_str";
				if (assignment != string_sign || string_sign == "")
					elm = " " + elm;
			}
			assignment += elm;
			free($1);
		}
		|
		INT {
			elm = string($1);
			if (assignment != string_sign || string_sign == "")
				elm = " " + elm;
			assignment += elm;
			free($1);
			status[1] = 1;
		}
		|
		FLOAT {
			elm = string($1);
			if (assignment != string_sign || string_sign == "")
				elm = " " + elm;
			assignment += elm;
			free($1);
			status[1] = 2;
		}
		|
		STRING {
			elm = string($1);
			if (assignment != string_sign || string_sign == "")
				elm = " " + elm;
			assignment += elm;
			free($1);
			status[1] = 3;
		}
		;

	sign:
		{string_sign = "";}
		|
		PLUS {string_sign = " +";}
		|
		MINUS {string_sign = " -";}
		;

	operator:
		PLUS {is_plus = true; assignment += " +";}
		|
		MINUS {is_plus = false; assignment += " -";}
		|
		POINT {is_plus = false; assignment += " *";}
		|
		SLASH {is_plus = false; assignment += " /";}
		;
%%

bool	search(string str, string ctrl)
{
	int	len = 0;
	for (int i = 0; i < str.length(); i++) {
		if (len < ctrl.length() && str[i] == ctrl[len])
			len++;
		else {
			if (len == ctrl.length() && str[i] == ',')
				return true;
			len = 0;
			while (i < str.length() && str[i] != ',')
				i++;
		}
	}

	return len == ctrl.length();
}

void	yyerror(string s){
	cerr<<"Error at line: "<<linenum<<endl;
	exit(1);
}

int	yywrap(){
	return 1;
}

int	main(int argc, char *argv[])
{
    /* Call the lexer, then quit. */
    yyin=fopen(argv[1],"r");
    yyparse();
    fclose(yyin);
	if (int_vars.length())
		cpp += "\tint " + int_vars + ";\n";
	if (float_vars.length())
		cpp += "\tfloat " + float_vars + ";\n";
	if (string_vars.length())
		cpp += "\tstring " + string_vars + ";\n";
	cpp += "\n";
	cpp += tmp;
	for (int i = maximum_number_of_tabs_required; i >= num_of_tabs + 1; i--) {
		for (int j = 0; j < i; j++)
			cpp += "\t";
		cpp += "}\n";
	}
	cpp += "}";
	cout << cpp << endl;
    return 0;
}
