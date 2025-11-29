'use client';

import React from 'react';
import { ChevronLeft, ChevronRight, Send } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Textarea } from '@/components/ui/textarea';
import { Label } from '@/components/ui/label';
import { QuizTimer } from './quiz-timer';
import { Quiz, QuizAnswer, QuestionType } from '@/types/active';
import { useActiveStore, activeSelectors } from '@/stores/activeStore';

interface QuizSessionProps {
  quiz: Quiz;
  onSubmit: (answers: QuizAnswer[]) => void;
}

export const QuizSession: React.FC<QuizSessionProps> = ({ quiz, onSubmit }) => {
  const { quizUIState, setSelectedAnswer, nextQuestion, previousQuestion } = useActiveStore();
  const currentQuestion = activeSelectors.getCurrentQuestion(useActiveStore());
  const progress = activeSelectors.getQuizProgress(useActiveStore());

  if (!currentQuestion) return null;

  const currentAnswer = quizUIState.selectedAnswers.get(currentQuestion.id);

  const handleSelectOption = (optionId: number) => {
    setSelectedAnswer(currentQuestion.id, {
      questionId: currentQuestion.id,
      selectedOptionIds: [optionId],
    });
  };

  const handleTextAnswer = (text: string) => {
    setSelectedAnswer(currentQuestion.id, {
      questionId: currentQuestion.id,
      textAnswer: text,
    });
  };

  const handleSubmitQuiz = () => {
    const answers = Array.from(quizUIState.selectedAnswers.values());
    onSubmit(answers);
  };

  const isLastQuestion = quizUIState.currentQuestionIndex === quiz.questions.length - 1;
  const canSubmit = quizUIState.selectedAnswers.size === quiz.questions.length;

  return (
    <div className="space-y-6">
      {quiz.timeLimitMinutes && (
        <QuizTimer
          totalSeconds={quiz.timeLimitMinutes * 60}
          onTimeUp={handleSubmitQuiz}
        />
      )}

      <Card>
        <CardHeader>
          <div className="flex items-center justify-between mb-2">
            <span className="text-sm text-gray-600">
              Question {quizUIState.currentQuestionIndex + 1} of {quiz.questions.length}
            </span>
            <span className="text-sm font-medium">{currentQuestion.points} points</span>
          </div>
          <Progress value={progress} className="h-2 mb-4" />
          <CardTitle className="text-xl">{currentQuestion.questionText}</CardTitle>
          {currentQuestion.description && (
            <p className="text-sm text-gray-600 mt-2">{currentQuestion.description}</p>
          )}
        </CardHeader>

        <CardContent className="space-y-4">
          {currentQuestion.questionType === QuestionType.MULTIPLE_CHOICE && (
            <RadioGroup
              value={currentAnswer?.selectedOptionIds?.[0]?.toString()}
              onValueChange={(value) => handleSelectOption(Number(value))}
            >
              {currentQuestion.options.map((option) => (
                <div key={option.id} className="flex items-center space-x-2 p-3 rounded-lg hover:bg-gray-50">
                  <RadioGroupItem value={option.id.toString()} id={`option-${option.id}`} />
                  <Label htmlFor={`option-${option.id}`} className="flex-1 cursor-pointer">
                    {option.optionText}
                  </Label>
                </div>
              ))}
            </RadioGroup>
          )}

          {currentQuestion.questionType === QuestionType.TRUE_FALSE && (
            <RadioGroup
              value={currentAnswer?.selectedOptionIds?.[0]?.toString()}
              onValueChange={(value) => handleSelectOption(Number(value))}
            >
              {currentQuestion.options.map((option) => (
                <div key={option.id} className="flex items-center space-x-2 p-3 rounded-lg hover:bg-gray-50">
                  <RadioGroupItem value={option.id.toString()} id={`option-${option.id}`} />
                  <Label htmlFor={`option-${option.id}`} className="flex-1 cursor-pointer">
                    {option.optionText}
                  </Label>
                </div>
              ))}
            </RadioGroup>
          )}

          {(currentQuestion.questionType === QuestionType.SHORT_ANSWER ||
            currentQuestion.questionType === QuestionType.FILL_BLANK) && (
            <Textarea
              placeholder="Type your answer..."
              value={currentAnswer?.textAnswer || ''}
              onChange={(e) => handleTextAnswer(e.target.value)}
              rows={3}
            />
          )}

          {currentQuestion.questionType === QuestionType.ESSAY && (
            <Textarea
              placeholder="Write your essay response..."
              value={currentAnswer?.textAnswer || ''}
              onChange={(e) => handleTextAnswer(e.target.value)}
              rows={10}
            />
          )}
        </CardContent>
      </Card>

      <div className="flex items-center justify-between">
        <Button
          variant="outline"
          onClick={previousQuestion}
          disabled={quizUIState.currentQuestionIndex === 0}
        >
          <ChevronLeft className="h-4 w-4 mr-1" />
          Previous
        </Button>

        <div className="text-sm text-gray-600">
          {quizUIState.selectedAnswers.size} / {quiz.questions.length} answered
        </div>

        {!isLastQuestion ? (
          <Button onClick={nextQuestion}>
            Next
            <ChevronRight className="h-4 w-4 ml-1" />
          </Button>
        ) : (
          <Button onClick={handleSubmitQuiz} disabled={!canSubmit}>
            <Send className="h-4 w-4 mr-1" />
            Submit Quiz
          </Button>
        )}
      </div>
    </div>
  );
};
