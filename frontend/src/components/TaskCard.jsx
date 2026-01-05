import './TaskCard.css';

const TaskCard = ({ task, projectName, onComplete }) => {
  const isCompleted = task.completed;

  return (
    <div className={`task-card ${isCompleted ? 'completed' : ''}`}>
      <div className="task-main">
        <div className="task-checkbox">
          {isCompleted ? (
            <span className="check-icon">✓</span>
          ) : (
            <button onClick={() => onComplete(task.id)} className="btn-complete">
              ○
            </button>
          )}
        </div>
        <div className="task-info">
          <h4 className={isCompleted ? 'completed-text' : ''}>{task.title}</h4>
          <p className="task-description">{task.description || 'No description'}</p>
        </div>
      </div>
      <div className="task-meta">
        <span className="project-tag">📁 {projectName}</span>
        {isCompleted && <span className="completed-badge">Completed</span>}
      </div>
    </div>
  );
};

export default TaskCard;
